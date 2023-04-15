package nl.marc_apps.tts

import nl.marc_apps.tts.errors.TextToSpeechInitialisationError

/**
 * Functions and properties that can be used to create new TTS instances
 * and check for compatibility issues.
 */
expect object TextToSpeech {
    val isSupported: Boolean

    val canChangeVolume: Boolean

    /**
     * Creates a new [TextToSpeechInstance].
     * Will call [callback] with null if TTS is not supported.
     */
    fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit)

    /**
     * Creates a new [TextToSpeechInstance].
     * Will do nothing and will not execute [callback] when TTS is not supported.
     */
    @Deprecated("Use create or createOrNull instead", ReplaceWith("createOrNull(context, callback)"))
    fun createOrNothing(context: Context, callback: (TextToSpeechInstance) -> Unit)

    /** Creates a new [TextToSpeechInstance]. */
    fun create(context: Context, callback: (Result<TextToSpeechInstance>) -> Unit)

    /**
     * Creates a new [TextToSpeechInstance].
     * @throws TextToSpeechInitialisationError
     */
    @Throws(TextToSpeechInitialisationError::class)
    suspend fun createOrThrow(context: Context): TextToSpeechInstance

    /**
     * Creates a new [TextToSpeechInstance].
     * Will return null if TTS is not supported.
     */
    suspend fun createOrNull(context: Context): TextToSpeechInstance?
}
