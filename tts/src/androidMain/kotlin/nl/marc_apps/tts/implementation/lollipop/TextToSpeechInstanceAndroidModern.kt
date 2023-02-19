package nl.marc_apps.tts.implementation.lollipop

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.Voice
import nl.marc_apps.tts.errors.*
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
internal class TextToSpeechInstanceAndroidModern(
    private var tts: TextToSpeech
) : TextToSpeechInstance {
    private val callbacks = mutableMapOf<UUID, Continuation<Unit>>()

    override val isSynthesizing = MutableStateFlow(false)

    override var volume = 100
        set(value) {
            if (value in 0..100) {
                field = value
            }
        }

    override var isMuted = false

    override var pitch = 1f
        set(value) {
            field = value
            tts.setPitch(value)
        }

    override var rate = 1f
        set(value) {
            field = value
            tts.setSpeechRate(value)
        }

    private val defaultVoice: android.speech.tts.Voice? = tts.voice ?: tts.defaultVoice

    override var currentVoice: Voice? = convertVoice(defaultVoice)
        set(value) {
            val result = (value as? VoiceAndroidModern)?.androidVoice?.let { tts.setVoice(it) }
            if (result != null && result >= 0) {
                field = value
            }
        }

    override val voices: Flow<Set<Voice>> = flow {
        emit(tts.voices?.map { convertVoice(it) }?.toSet() ?: emptySet())
    }

    @JvmName("convertNullableVoice")
    private fun convertVoice(voice: android.speech.tts.Voice?): VoiceAndroidModern? {
        return if (voice == null) null else convertVoice(voice)
    }

    private fun convertVoice(voice: android.speech.tts.Voice): VoiceAndroidModern {
        return VoiceAndroidModern(voice, voice == tts.defaultVoice)
    }

    init {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            private fun getContinuationId(utteranceId: String?): UUID? {
                return if (utteranceId == null) null else try {
                    return UUID.fromString(utteranceId)
                } catch (_: Throwable) {
                    null
                }
            }

            override fun onStart(utteranceId: String?) {
                isSynthesizing.value = true
            }

            override fun onDone(utteranceId: String?) {
                isSynthesizing.value = false
                callbacks.remove(getContinuationId(utteranceId))?.resume(Unit)
            }

            @Deprecated("Use onError(utteranceId, errorCode) instead")
            override fun onError(utteranceId: String?) {
                isSynthesizing.value = false
                callbacks.remove(getContinuationId(utteranceId))?.resumeWithException(TextToSpeechServiceFailureError())
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                isSynthesizing.value = false
                callbacks.remove(getContinuationId(utteranceId))?.resumeWithException(mapErrorCodeToThrowable(errorCode))
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                isSynthesizing.value = false
                val continuation = callbacks.remove(getContinuationId(utteranceId))
                if (interrupted) {
                    continuation?.resume(Unit)
                } else {
                    continuation?.resumeWithException(TextToSpeechSynthesisInterruptedError())
                }
            }
        })
    }

    override fun enqueue(text: String) {
        val utteranceId = UUID.randomUUID()

        val params = Bundle()
        params.putFloat(KEY_PARAM_VOLUME, if (isMuted) 0f else volume / 100f)
        params.putString(KEY_PARAM_UTTERANCE_ID, utteranceId.toString())

        tts.speak(text, TextToSpeech.QUEUE_ADD, params, utteranceId.toString())
    }

    override suspend fun say(text: String) {
        val utteranceId = UUID.randomUUID()

        val params = Bundle()
        params.putFloat(KEY_PARAM_VOLUME, if (isMuted) 0f else volume / 100f)
        params.putString(KEY_PARAM_UTTERANCE_ID, utteranceId.toString())

        suspendCoroutine { continuation ->
            callbacks += utteranceId to continuation

            try {
                tts.speak(text, TextToSpeech.QUEUE_ADD, params, utteranceId.toString())
            } catch (error: Throwable) {
                callbacks.remove(utteranceId)
                continuation.resumeWithException(error)
            }
        }
    }

    override operator fun plusAssign(text: String) {
        enqueue(text)
    }

    override fun stop() {
        tts.stop()
    }

    override fun close() {
        stop()
        tts.setOnUtteranceProgressListener(null)
        tts.shutdown()
        callbacks.clear()
    }

    companion object {
        private const val KEY_PARAM_VOLUME = "volume"

        private const val KEY_PARAM_UTTERANCE_ID = "utteranceId"

        /** Denotes a failure of a TTS engine to synthesize the given input. */
        private const val ERROR_SYNTHESIS = -3

        /** Denotes a failure of a TTS service. */
        private const val ERROR_SERVICE = -4

        /** Denotes a failure related to the output (audio device or a file). */
        private const val ERROR_OUTPUT = -5

        /** Denotes a failure caused by a network connectivity problems. */
        private const val ERROR_NETWORK = -6

        /** Denotes a failure caused by network timeout.*/
        private const val ERROR_NETWORK_TIMEOUT = -7

        /** Denotes a failure caused by an invalid request. */
        private const val ERROR_INVALID_REQUEST = -8

        /** Denotes a failure caused by an unfinished download of the voice data. */
        private const val ERROR_NOT_INSTALLED_YET = -9

        private fun mapErrorCodeToThrowable(errorCode: Int): TextToSpeechSynthesisError {
            return when(errorCode) {
                ERROR_SYNTHESIS, ERROR_INVALID_REQUEST -> TextToSpeechRequestInvalidError()
                ERROR_SERVICE, ERROR_OUTPUT, ERROR_NOT_INSTALLED_YET -> TextToSpeechServiceFailureError()
                ERROR_NETWORK, ERROR_NETWORK_TIMEOUT -> NetworkConnectivityError()
                else -> TextToSpeechServiceFailureError()
            }
        }
    }
}
