package js_interop

expect abstract class EventTargetCommon

expect fun EventTargetCommon.addEventListenerWithoutOptions(type: String, callback: EventListener?)

expect fun EventTargetCommon.removeEventListenerWithoutOptions(type: String, callback: EventListener?)
