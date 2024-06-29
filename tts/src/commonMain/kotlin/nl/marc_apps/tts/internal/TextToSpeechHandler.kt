package nl.marc_apps.tts.internal

import nl.marc_apps.tts.Voice

interface TextToSpeechHandler : AutoCloseable {
    val voice : Voice?

    val voices: Sequence<Voice>

    fun clearQueue()
}
