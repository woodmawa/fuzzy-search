package EventBus.Implementation

class Subscriber {
    void onMessage (String path, def message) {
        println "received message : $message"
    }
}

def subs = new Subscriber()

EventBus eb = new EventBus()

eb.addSubscriber("any.event", subs)

eb.start()
eb.notifyMessage "any.event", "hellooo"

eb.processMessage()


eb.stop()


