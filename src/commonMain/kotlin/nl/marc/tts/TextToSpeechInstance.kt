package nl.marc.tts

expect interface TextToSpeechInstance {
    var volume: Int

    fun say(text: String, clearQueue: Boolean = false)

    fun close()
}
