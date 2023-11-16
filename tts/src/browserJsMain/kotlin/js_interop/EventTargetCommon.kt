package js_interop

actual typealias EventTargetCommon = org.w3c.dom.events.EventTarget

actual fun EventTargetCommon.addEventListenerWithoutOptions(type: String, callback: EventListener?) {
    addEventListener(type, callback)
}

actual fun EventTargetCommon.removeEventListenerWithoutOptions(type: String, callback: EventListener?) {
    removeEventListener(type, callback)
}