package nl.marc_apps.tts

/** A TTS instance. Should be [close]d when no longer in use. */
actual interface TextToSpeechInstance : Closeable {
    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
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
    actual fun say(text: String, clearQueue: Boolean, callback: (Result<Status>) -> Unit)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    actual suspend fun say(text: String, clearQueue: Boolean, resumeOnStatus: Status)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    actual operator fun plusAssign(text: String)

    /** Clears the internal queue, but doesn't close used resources. */
    actual fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    actual override fun close()

    actual enum class Status {
        STARTED, FINISHED
    }

    actual companion object {
        actual val VOLUME_MIN = 0

        actual val VOLUME_MAX = 100

        actual val VOLUME_DEFAULT = VOLUME_MAX

        actual val VOICE_PITCH_DEFAULT = 1f

        actual val VOICE_RATE_DEFAULT = 1f
    }
}
