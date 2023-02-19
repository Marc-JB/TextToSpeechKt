package nl.marc_apps.tts

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TextToSpeechInstance {
    val isSynthesizing: StateFlow<Boolean>

    var volume: Int

    var isMuted: Boolean

    var pitch: Float

    var rate: Float

    var currentVoice: Voice?

    val voices: Flow<Set<Voice>>

    fun enqueue(text: String)

    suspend fun say(text: String)

    operator fun plusAssign(text: String)

    fun stop()

    fun close()
}
