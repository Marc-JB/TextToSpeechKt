package nl.marc.tts

expect interface TextToSpeechInstance {
    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     */
    var volume: Int

    var isMuted: Boolean

    var pitch: Float

    var rate: Float

    fun say(text: String, clearQueue: Boolean = false)

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    fun close()
}
