package nl.marc.tts

expect interface TextToSpeechInstance {
    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     * Changes only affect new calls to the [say] method.
     */
    var volume: Int

    /**
     * Alternative to setting [volume] to zero.
     * Setting this to true (and back to false) doesn't change the value of [volume].
     * Changes only affect new calls to the [say] method.
     */
    var isMuted: Boolean

    var pitch: Float

    var rate: Float

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    val language: String

    /**
     * Behaviour of this method:
     *
     * 1A) [clearQueue] is true: Clears the internal queue (like the [stop] method).
     * 1B) [clearQueue] is false: Retains the internal queue.
     *
     * 2A) [isMuted] is true, or [volume] is zero: No text is added to the queue.
     * 2B) [isMuted] is false and [volume] is above zero: Adds the text with [volume], [rate] and [pitch] to the internal queue.
     */
    fun say(text: String, clearQueue: Boolean = false)

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    fun close()
}
