package nl.marc_apps.tts

expect class TextToSpeechFactory {
    val isSupported: Boolean

    @Throws(RuntimeException::class)
    suspend fun create(): TextToSpeechInstance

    suspend fun createOrNull(): TextToSpeechInstance?
}
