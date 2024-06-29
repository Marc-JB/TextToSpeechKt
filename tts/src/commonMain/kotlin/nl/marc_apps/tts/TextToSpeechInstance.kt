package nl.marc_apps.tts

import kotlinx.coroutines.flow.StateFlow

/** A TTS instance. Should be [close]d when no longer in use. */
interface TextToSpeechInstance : AutoCloseable {
    val currentState: StateFlow<State>

    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
     * Changes only affect new calls to the [say] method.
     */
    var volume: Int
    val currentVolume: StateFlow<Int>

    var pitch: Float
    val currentPitch: StateFlow<Float>

    var rate: Float
    val currentRate: StateFlow<Float>

    val voice: Voice?

    val voices: Sequence<Voice>

    /** Adds the given [text] to the internal queue. */
    fun enqueue(text: String)

    /** Adds the given [text] to the internal queue. */
    fun say(text: String, callback: (Result<Unit>) -> Unit)

    /** Adds the given [text] to the internal queue. */
    suspend fun say(text: String)

    /** Adds the given [text] to the internal queue. */
    operator fun plusAssign(text: String)

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop()

    /** Clears the internal queue and closes used resources (if possible) */
    override fun close()

    enum class State {
        LOADING,
        SYNTHESIZING,
        QUEUE_EMPTY
    }

    companion object {
        const val VOLUME_MIN = 0

        const val VOLUME_MAX = 100

        const val VOLUME_DEFAULT = VOLUME_MAX

        const val VOICE_PITCH_DEFAULT = 1f

        const val VOICE_RATE_DEFAULT = 1f
    }
}
