package js_interop

actual external interface EventListener {
    actual fun handleEvent(event: Event)
}