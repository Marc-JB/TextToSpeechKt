package nl.marc_apps.tts.internal

interface CallbackQueueHandler {
    fun createUtteranceId(): Any

    fun enqueue(text: String, utteranceId: Any, options: EnqueueOptions = EnqueueOptions())

    fun registerListeners(onStart: (Any) -> Unit, onComplete: (Any, Result<Unit>) -> Unit)
}
