package testScripts

import grails.async.DelegateAsync
import grails.async.Promise
import grails.async.PromiseList
import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.DataflowVariable
import org.grails.async.factory.gpars.GparsPromise

import static grails.async.Promises.*

DataflowVariable var = new DataflowVariable<>()
DataflowVariable res = new DataflowVariable<>()
DataflowVariable res2 = new DataflowVariable<>()

//data flow chaining using then - each then returns a new promise (aka dfv)
var.then {it*2} then {it+1} then {res << it}

var << 4

println "df result : $res.val"

//same idea but done with Grails promise (defaults to delegated gpars df
Promise p = task {2+3}


p.then {it * 2} then {it+10} then {res2 << it}

println "promise is of type ${p.getClass()} with chained output value ${res2.get()}"

def p1 = task {2*2}
def p2 = task {4*4}
def p3 = task {8*8}

println waitAll (p1,p2,p3)

PromiseList list  = new PromiseList()
list << {2*2} << {4*4}
list << {8*8}
list.onComplete { List l -> println "promiseList looks like : $l"}

list.get()

/**
 * grails : synchronous service and async version that reuses same impl
 */

class Service {

    List things = ["aaa", "aa",  "m", "z"]

    List findThings (String match) {
        def result = []
        //def thingsList = things.inject ([]) {res, thing -> if (thing.contains (match.toLowerCase())) res << thing}
        things.findAll {String thing ->
            if (thing.contains (match.toLowerCase()) )
                 result << thing }
        result
        //thingsList - returned null
    }
}

class AsyncService {
    @DelegateAsync Service service = new Service()
}

def ats = new AsyncService()

//async call on asyncService returns promise
def thingRes = ats.findThings ("AA").onComplete {List thingsList -> println "async calc thinglist : $thingsList"}

thingRes.get()

/**
 * gpars :  dataflow queues, demonstrating many to 1
 *
 */

def words = ["on", "a", "good", "day", "live", "is", "good"]
def words2 = ["Gandalf", "is", "bad"]
DataflowQueue queue = new DataflowQueue()

GparsPromise t1 = task {
    for (word in words) {
        //add word to queue
        queue << word.toUpperCase()
    }
    "done"
}

GparsPromise t2 = task {
    for (word in words2) {
        //add word to queue from second list
        queue << word.toUpperCase()
    }
    "done"
}

def t3 = task {
    println "read word from queue"
    while (true) println queue.val  //read from queue in loop
}

/*queue.
def val = queue.getVal()
def len = queue.length()*/
//queue.whenBound { println "queue val  $it"}

assert t1.get() == "done"
assert t2.get() == "done"