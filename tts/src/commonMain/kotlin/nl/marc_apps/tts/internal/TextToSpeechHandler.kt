package nl.marc_apps.tts.internal

interface TextToSpeechHandler : AutoCloseable {
    fun clearQueue()
}
