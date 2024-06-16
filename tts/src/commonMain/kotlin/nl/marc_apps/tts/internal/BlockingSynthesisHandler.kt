package nl.marc_apps.tts.internal

interface BlockingSynthesisHandler {
    fun enqueue(text: String, clearQueue: Boolean)
}
