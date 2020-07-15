package nl.marc.tts

expect interface TextToSpeechInstance {
    var volume: Int

    var isMuted: Boolean

    fun say(text: String, clearQueue: Boolean = false)

    fun stop()

    fun close()
}
