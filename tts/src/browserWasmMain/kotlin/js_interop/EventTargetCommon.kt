package js_interop

actual abstract class EventTargetCommon {
    abstract fun addEventListener(type: String, callback: EventListener?)

    abstract fun removeEventListener(type: String, callback: EventListener?)
}

actual fun EventTargetCommon.addEventListenerWithoutOptions(type: String, callback: EventListener?) {
    addEventListener(type, callback)
}

actual fun EventTargetCommon.removeEventListenerWithoutOptions(type: String, callback: EventListener?) {
    removeEventListener(type, callback)
}
