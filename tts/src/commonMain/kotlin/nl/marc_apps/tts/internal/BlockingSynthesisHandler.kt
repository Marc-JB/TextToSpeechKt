package nl.marc_apps.tts.internal

interface BlockingSynthesisHandler {
    fun enqueue(text: String, options: EnqueueOptions = EnqueueOptions())
}
