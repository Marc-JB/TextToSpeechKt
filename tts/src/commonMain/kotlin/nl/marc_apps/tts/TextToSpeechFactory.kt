package nl.marc_apps.tts

/**
 * Factory class to create a Text-to-Speech instance.
 */
expect class TextToSpeechFactory {
    val isSupported: Boolean

    val canChangeVolume: Boolean

    suspend fun create(): Result<TextToSpeechInstance>

    @Throws(RuntimeException::class)
    suspend fun createOrThrow(): TextToSpeechInstance

    suspend fun createOrNull(): TextToSpeechInstance?
}
