package EventBus.Implementation

class Subscriber {
    //invoked by dynamic dispatch
    void onMessage (String topic, message) {
        println "received message as def  : $message"
    }

    def onMessage (String topic, String message) {
        println "received message as String : $message"

    }
}

def subs = new Subscriber()

EventBus eb = new EventBus()

eb.addSubscriber("any.event", subs)

eb.start()
eb.notifyMessage "any.event", "hellooo"
//eb.notifyMessage "any.event", EventBusProcessor.STOP


eb.stop()


