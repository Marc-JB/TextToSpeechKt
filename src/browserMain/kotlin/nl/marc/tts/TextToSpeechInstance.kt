package nl.marc.tts

/** A TTS instance. Should be [close]d when no longer in use. */
actual interface TextToSpeechInstance {
    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     * Changes only affect new calls to the [say] method.
     */
    actual var volume: Int

    /**
     * Alternative to setting [volume] to zero.
     * Setting this to true (and back to false) doesn't change the value of [volume].
     * Changes only affect new calls to the [say] method.
     */
    actual var isMuted: Boolean

    actual var pitch: Float

    actual var rate: Float

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    actual val language: String

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    actual fun enqueue(text: String, clearQueue: Boolean)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    actual operator fun plusAssign(text: String)

    /** Clears the internal queue, but doesn't close used resources. */
    actual fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    actual fun close()
}
