package js_interop

abstract external class EventTarget {
    fun addEventListener(type: String, callback: EventListener?)

    fun removeEventListener(type: String, callback: EventListener?)
}