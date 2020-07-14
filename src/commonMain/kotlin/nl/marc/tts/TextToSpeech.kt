package nl.marc.tts

expect object TextToSpeech {
    val isSupported: Boolean

    val canChangeVolume: Boolean
}
