package nl.marc.tts

/**
 * Functions and properties that can be used to create new TTS instances
 * and check for compatibility issues.
 */
expect object TextToSpeech {
    val isSupported: Boolean

    val canChangeVolume: Boolean

    /**
     * Creates a new [TextToSpeech] instance.
     * @throws TextToSpeechNotSupportedException when TTS is not supported.
     */
    @Throws(TextToSpeechNotSupportedException::class)
    fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit)

    /**
     * Creates a new [TextToSpeech] instance.
     * Will call [callback] with null if TTS is not supported.
     */
    fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit)
}
