package nl.marc.tts

@JsExport
actual object TextToSpeech {
    actual val isSupported = js("\"speechSynthesis\" in window") as Boolean

    actual val canChangeVolume = true

    /**
     * Creates a new [TextToSpeech] instance.
     * Will throw an [TextToSpeechNotSupportedException] if TTS is not supported.
     */
    fun createOrThrow(): TextToSpeechInstance {
        if(isSupported) return TextToSpeechJS()
        else throw TextToSpeechNotSupportedException()
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will return null if TTS is not supported.
     */
    fun createOrNull(): TextToSpeechInstance? {
        return if(isSupported) TextToSpeechJS() else null
    }
}
