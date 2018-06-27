package EventBus.Implementation

import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.Promise

import java.util.concurrent.atomic.AtomicLong

import static groovyx.gpars.dataflow.Dataflow.task

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class EventBus {

    ConcurrentHashMap messagePathSubscribers = new ConcurrentHashMap()

    final DataflowQueue inMessageQueue = new DataflowQueue ()
    Agent eventId = new Agent (0L)

    Promise eventProcessor

    void notifyMessage (String sMessageType, def message) {
        /*def num = eventId.val
        List m = [num,[sMessageType,message] ]
        inMessageQueue << m*/
        task {
            eventId.send  {updateValue it + 1}  //increment message counter
            //def num = eventId.val
            //def m = [num,[sMessageType,message] ]
            inMessageQueue << [eventId.val,[sMessageType,message] ]
        }
    }

    void addSubscriber (String sMessagePath, def subscriberInstance ) {
        def subscribers = messagePathSubscribers.get(sMessagePath)
        if (!subscribers) {
            def subscriberList = [subscriberInstance]
            messagePathSubscribers.putIfAbsent(sMessagePath, subscriberList)
        }
        else {
            subscribers << subscriberInstance
            messagePathSubscribers.put(subscribers)
        }

    }

    //hack manual step through
    void processMessage () {
        Map event = inMessageQueue.val
        def messagePath = event[1][0]
        def message = event[1][1]

        def subscriberList = messagePathSubscribers[messagePath]
        subscriberList.each {it.onMessage (messagePath, message)}

    }

    void start() {
        /*
        eventProcessor = task {
            while (true) {
                Map event = inMessageQueue.val
                def messagePath = event[1][0]
                def message = event[1][1]

                def subscriberList = messagePathSubscribers[messagePath]
                subscriberList.each {it.onMessage (messagePath, message)}

            }
        }*/
    }

    void stop() {
        //eventProcessor.
    }
}
