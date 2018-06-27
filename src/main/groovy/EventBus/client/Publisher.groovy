package EventBus.client

trait Publisher {
    void notify (String sMessagePath, def targetMessage = null, Map headers ) {

    }
}
