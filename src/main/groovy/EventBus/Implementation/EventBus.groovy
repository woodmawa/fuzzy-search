package EventBus.Implementation

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.NoArgGenerator
import groovy.util.logging.Log
import groovy.util.logging.Log4j
import groovy.util.logging.Log4j2
import groovy.util.logging.Slf4j
import groovyx.gpars.GParsPool
import groovyx.gpars.activeobject.ActiveMethod
import groovyx.gpars.activeobject.ActiveObject
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.Promise
import util.UuidUtil

import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.stream.Collectors
import java.util.stream.*

import static groovyx.gpars.dataflow.Dataflow.task

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

enum EventBusProcessor {
    START,
    STOP
}

enum EventBusStatus {
    STARTING,
    RUNNING,
    STOPPING,
    STOPPED
}

@Slf4j
class EventBus {

    /**
     * index is apth string to subscribe on , value is array of subscribed listeners
     * each expected to have an onMessage method that can be called
     */
    ConcurrentHashMap messagePathSubscribers = new ConcurrentHashMap()
    final DataflowQueue inMessageQueue = new DataflowQueue ()

    Agent status = new Agent (EventBusStatus.STOPPED)

    Agent eventId = new Agent ()
    final done = new AtomicBoolean()  //defaults to false

    Promise eventProcessor
    NoArgGenerator timeBasedGenerator = Generators.timeBasedGenerator()
    private static final long START_EPOCH = -12219292800000L;


    void notifyEvent (String topic, def message) {
        //do as async task
        task {
            UUID tuid = UuidUtil.getTimeBasedUuid()

            eventId.send  {updateValue it = tuid}  //set the eventId as time based guid
            //add message to queue with unique id and array of topic and message
            inMessageQueue << [eventId.val, [topic,message] ]

        }
    }

    void addSubscriber (String topic, def subscriberInstance ) {
        //see if path string already registered as key
        List subscribers = messagePathSubscribers.get(topic)
        if (!subscribers) {
            log.debug "addSubscriber: adding first subscriber on topic : $topic"
            messagePathSubscribers.putIfAbsent(topic, [subscriberInstance])
        }
        else {
            if (!subscribers.contains(subscriberInstance)) {
                log.debug "addSubscriber: adding another subscriber to topic : $topic"

                subscribers << subscriberInstance
                messagePathSubscribers.put(topic, subscribers)
            }

        }

    }

    void removeSubscriber (String topic, def subscriberInstance) {
        List subscribers = messagePathSubscribers.get(topic)
        if (subscribers) {
            List reducedSubscribersList = subscribers.stream().filter {!(it.is(subscriberInstance))}.collect(Collectors.toList());
            log.debug "removeSubscriber: removed subscriber to topic : $topic, remaining subscribers : $reducedSubscribersList"

            //is same list or another - do i n3eed to resave to map?
            messagePathSubscribers.put(topic, reducedSubscribersList)
        }
    }

    void clearAllSubscribers (String topic) {
        log.debug "clearAllSubscriber: removed all subscriber to topic : $topic"

        messagePathSubscribers.put(topic, [])
    }


    //private method to deliver the message to subscribers
    private void  processMessage (String topic, message) {
        assert topic
        def subscriberList = messagePathSubscribers[topic]

        Closure dispatchMessage = {subscriber ->
            boolean ans = subscriber.respondsTo("onMessage")
            if (subscriber.respondsTo("onMessage" )) {
                subscriber.onMessage(topic, message)
            }  else {
                log.debug "processMessage: no matching onMessage method found on subscriber"

            }
        }
        //parallel execution to dispatch message to all subscribers in the list
        GParsPool.withPool {
            subscriberList.eachParallel { dispatchMessage(it) }
        }
        //doesnt seem to work ! Stream.of(subscriberList).parallel().forEach ({dispatchMessage(it)})

    }

    void start() {
        //start a task - but it will stop if message is "STOP"
        status.send {updateValue it =  EventBusStatus.STARTING}
        eventProcessor = task {

            log.debug "start: event bus processor started, status : $status.val "
            status.send {updateValue it =  EventBusStatus.RUNNING}

            while (!done.get()) {
                List eventRecord = inMessageQueue.val
                UUID eventId = eventRecord [0]
                def topic = eventRecord[1][0]
                def message = eventRecord[1][1]

                if (message.class == EventBusProcessor) {
                    if (message == EventBusProcessor.STOP) {
                        status.send {updateValue it =  EventBusStatus.STOPPING}
                        done.set(true)
                        log.debug "start : in event loop, EventBus processor saw STOP message , EventBus now : $status.val "
                        break
                    }
                }
                processMessage (topic, message)
            }
            status.send {updateValue it =  EventBusStatus.STOPPED}
            log.debug "start: EventBus processor now stopped, state : $status.val , done flag is : ${done.get()}"
            return status.val
        }
    }

    void stop() {
        //will step even if there messages left in queue to process
        //seems to need this long for start task to kick in and start processing
        // if go earlier done flag gets set before a chance to read the queue - so delay added
        //sleep (500)
        /*def t = task {
            done.set(true)
            status.send {updateValue it =  EventBusStatus.STOPPING}
            log.debug "stopTask completed,  returning status : $status.val"
            status.val
        }*/
            log.debug "stop method main thread: sending STOP message to queue "
            notifyEvent("eventBus.stop", EventBusProcessor.STOP)

        //wait till task stops
        //stopTask.then {println "-> stop method main thread: set done flag to : ${done.get()} status: $it"}
        eventProcessor.then {println "-> eventProcessor task: stopped processing queue, status : $it"}
        //[stopTask,eventProcessor]*.join()
        eventProcessor.join()
        log.debug "stop method main thread: stopping the processor, remaining messages in queue ${inMessageQueue.length()} "

    }
}
