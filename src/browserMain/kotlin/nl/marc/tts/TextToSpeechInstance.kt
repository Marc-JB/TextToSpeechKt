package nl.marc.tts

@JsExport
actual interface TextToSpeechInstance {
    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     */
    actual var volume: Int

    actual var isMuted: Boolean

    actual fun say(text: String, clearQueue: Boolean)

    /** Clears the internal queue, but doesn't close used resources. */
    actual fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    actual fun close()
}
