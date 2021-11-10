package nl.marc_apps.tts

/** A TTS instance. Should be [close]d when no longer in use. */
interface TextToSpeechInstance : Closeable {
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

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun enqueue(text: String, clearQueue: Boolean = false)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun say(text: String, clearQueue: Boolean = false, callback: (Result<Status>) -> Unit)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    suspend fun say(text: String, clearQueue: Boolean = false, resumeOnStatus: Status = Status.FINISHED)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    operator fun plusAssign(text: String)

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    override fun close()

    enum class Status {
        STARTED, FINISHED
    }
}
