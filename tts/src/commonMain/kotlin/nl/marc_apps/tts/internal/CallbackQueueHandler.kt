package nl.marc_apps.tts.internal

interface CallbackQueueHandler {
    fun createUtteranceId(): Any

    fun enqueue(text: String, clearQueue: Boolean, utteranceId: Any)

    fun registerListeners(onStart: (Any) -> Unit, onComplete: (Any, Result<Unit>) -> Unit)
}
