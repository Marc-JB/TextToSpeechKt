package nl.marc_apps.tts

import kotlinx.coroutines.flow.StateFlow
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi

/** A TTS instance. Should be [close]d when no longer in use. */
interface TextToSpeechInstance : Closeable {
    val isSynthesizing: StateFlow<Boolean>

    /**
     * Value indicating if the engine is warming up.
     * Is true after [enqueue] or [say] has been called the first time,
     * but before [isSynthesizing] is true. Is false otherwise.
     */
    val isWarmingUp: StateFlow<Boolean>

    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
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

    @ExperimentalVoiceApi
    var currentVoice: Voice?

    @ExperimentalVoiceApi
    val voices: Sequence<Voice>

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun enqueue(text: String, clearQueue: Boolean = false)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun say(text: String, clearQueue: Boolean = false, callback: (Result<Unit>) -> Unit)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    suspend fun say(text: String, clearQueue: Boolean = false)

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    operator fun plusAssign(text: String)

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    override fun close()

    companion object {
        const val VOLUME_MIN = 0

        const val VOLUME_MAX = 100

        const val VOLUME_DEFAULT = VOLUME_MAX

        const val VOICE_PITCH_DEFAULT = 1f

        const val VOICE_RATE_DEFAULT = 1f
    }
}
