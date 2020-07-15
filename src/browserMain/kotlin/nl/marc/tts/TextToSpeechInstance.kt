package nl.marc.tts

@JsExport
actual interface TextToSpeechInstance {
    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     */
    actual var volume: Int

    actual var isMuted: Boolean

    actual var pitch: Float

    actual var rate: Float

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    actual val language: String

    actual fun say(text: String, clearQueue: Boolean)

    /** Clears the internal queue, but doesn't close used resources. */
    actual fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    actual fun close()
}
