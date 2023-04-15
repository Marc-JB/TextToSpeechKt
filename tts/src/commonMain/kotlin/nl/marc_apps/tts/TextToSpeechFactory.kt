package nl.marc_apps.tts

expect class TextToSpeechFactory {
    val isSupported: Boolean

    val canChangeVolume: Boolean

    suspend fun create(): Result<TextToSpeechInstance>

    @Throws(RuntimeException::class)
    suspend fun createOrThrow(): TextToSpeechInstance

    suspend fun createOrNull(): TextToSpeechInstance?
}
