package nl.marc.tts

expect object TextToSpeech {
    val isSupported: Boolean

    val canChangeVolume: Boolean

    @Throws(TextToSpeechNotSupportedException::class)
    fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit)

    fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit)
}
