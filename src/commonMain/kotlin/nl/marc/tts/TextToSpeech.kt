package nl.marc.tts

import platform.Throws

/**
 * Functions and properties that can be used to create new TTS instances
 * and check for compatibility issues.
 */
expect object TextToSpeech {
    val isSupported: Boolean

    val canChangeVolume: Boolean

    /**
     * Creates a new [TextToSpeechInstance].
     * @throws TextToSpeechInitialisationError
     */
    @Deprecated("Use TextToSpeech.create(ctx, cb)")
    @Throws(TextToSpeechInitialisationError::class)
    fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit)

    /**
     * Creates a new [TextToSpeechInstance].
     * Will call [callback] with null if TTS is not supported.
     */
    fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit)

    /**
     * Creates a new [TextToSpeechInstance].
     * Will do nothing and will not execute [callback] when TTS is not supported.
     */
    fun createOrNothing(context: Context, callback: (TextToSpeechInstance) -> Unit)

    /** Creates a new [TextToSpeechInstance]. */
    fun create(context: Context, callback: (Result<TextToSpeechInstance>) -> Unit)
}
