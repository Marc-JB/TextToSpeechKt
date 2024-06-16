package nl.marc_apps.tts.internal

interface CallbackQueueHandler : TextToSpeechHandler {
    fun createUtteranceId(): Any

    fun enqueue(text: String, clearQueue: Boolean)

    fun registerListeners(onStart: (Any) -> Unit, onComplete: (Any, Result<Unit>) -> Unit)
}
