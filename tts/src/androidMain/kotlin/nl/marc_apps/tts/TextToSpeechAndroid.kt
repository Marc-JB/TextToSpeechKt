@file:Suppress("unused")

package nl.marc_apps.tts

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import nl.marc_apps.tts.errors.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.speech.tts.TextToSpeech as AndroidTTS

/** A TTS instance. Should be [close]d when no longer in use. */
@TargetApi(VERSION_CODES.DONUT)
internal class TextToSpeechAndroid(private var tts: AndroidTTS?) : TextToSpeechInstance {
    private val callbacks = mutableMapOf<Int, (Result<TextToSpeechInstance.Status>) -> Unit>()

    private val internalVolume: Float
        get() = if(!isMuted) volume / 100f else 0f

    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
     * Changes only affect new calls to the [say] method.
     */
    @IntRange(from = TextToSpeechInstance.VOLUME_MIN.toLong(), to = TextToSpeechInstance.VOLUME_MAX.toLong())
    override var volume: Int = TextToSpeechInstance.VOLUME_DEFAULT
        set(value) {
            if(TextToSpeech.canChangeVolume) {
                field = when {
                    value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                    value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                    else -> value
                }
            }
        }

    /**
     * Alternative to setting [volume] to zero.
     * Setting this to true (and back to false) doesn't change the value of [volume].
     * Changes only affect new calls to the [say] method.
     */
    override var isMuted: Boolean = false

    override var pitch: Float = TextToSpeechInstance.VOICE_PITCH_DEFAULT
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    override var rate: Float = TextToSpeechInstance.VOICE_RATE_DEFAULT
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    private val voiceLocale: Locale
        get() = (if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) tts?.voice?.locale else tts?.language) ?: Locale.getDefault()

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    override val language: String
        get() = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) voiceLocale.toLanguageTag() else voiceLocale.language

    init {
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setProgressListeners()
        } else {
            setProgressListenersLegacy()
        }
    }

    private fun setProgressListenersLegacy() {
        tts?.setOnUtteranceCompletedListener {
            onTtsStatusUpdate(it?.toInt() ?: -1, TextToSpeechInstance.Status.FINISHED)
        }
    }

    @RequiresApi(VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun setProgressListeners() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onTtsStatusUpdate(utteranceId?.toInt() ?: -1, TextToSpeechInstance.Status.STARTED)
            }

            override fun onDone(utteranceId: String?) {
                onTtsStatusUpdate(utteranceId?.toInt() ?: -1, TextToSpeechInstance.Status.FINISHED)
            }

            override fun onError(utteranceId: String?) {
                onTtsStatusUpdate(utteranceId?.toInt() ?: -1, UnknownTextToSpeechSynthesisError())
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                onTtsStatusUpdate(utteranceId?.toInt() ?: -1, mapErrorCodeToThrowable(errorCode))
            }

            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                onTtsStatusUpdate(utteranceId?.toInt() ?: -1, TextToSpeechSynthesisInterruptedError())
            }
        })
    }

    private fun mapErrorCodeToThrowable(errorCode: Int): TextToSpeechSynthesisError {
        return when(errorCode) {
            ERROR_SYNTHESIS -> TextToSpeechFlawedTextInputError()
            ERROR_SERVICE -> TextToSpeechServiceFailureError()
            ERROR_OUTPUT -> DeviceAudioOutputError()
            ERROR_NETWORK -> NetworkConnectivityError()
            ERROR_NETWORK_TIMEOUT -> NetworkTimeoutError()
            ERROR_INVALID_REQUEST -> TextToSpeechRequestInvalidError()
            ERROR_NOT_INSTALLED_YET -> TextToSpeechEngineUnavailableError()
            else -> UnknownTextToSpeechSynthesisError()
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun enqueue(text: String, clearQueue: Boolean) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            return
        }

        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val params = Bundle()
            params.putFloat(KEY_PARAM_VOLUME, internalVolume)
            tts?.speak(text, queueMode, params, null)
        } else {
            val params = hashMapOf(
                KEY_PARAM_VOLUME to internalVolume.toString()
            )
            tts?.speak(text, queueMode, params)
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun say(text: String, clearQueue: Boolean, callback: (Result<TextToSpeechInstance.Status>) -> Unit) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            callback(Result.success(TextToSpeechInstance.Status.FINISHED))
            return
        }

        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        val utteranceId = arrayOf(System.currentTimeMillis(), text).contentHashCode()

        callbacks += utteranceId to {
            callback(it)

            if (it.isFailure || it.getOrNull() == TextToSpeechInstance.Status.FINISHED) {
                callbacks.remove(utteranceId)
            }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val params = Bundle()
            params.putFloat(KEY_PARAM_VOLUME, internalVolume)
            params.putInt(KEY_PARAM_UTTERANCE_ID, utteranceId)
            tts?.speak(text, queueMode, params, utteranceId.toString())
        } else {
            val params = hashMapOf(
                KEY_PARAM_VOLUME to internalVolume.toString(),
                KEY_PARAM_UTTERANCE_ID to utteranceId.toString()
            )
            tts?.speak(text, queueMode, params)
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override suspend fun say(text: String, clearQueue: Boolean, resumeOnStatus: TextToSpeechInstance.Status) {
        suspendCoroutine<Unit> { cont ->
            say(text, clearQueue) {
                if (it.isSuccess && it.getOrNull() in arrayOf(resumeOnStatus, TextToSpeechInstance.Status.FINISHED)) {
                    cont.resume(Unit)
                } else if (it.isFailure) {
                    val error = it.exceptionOrNull() ?: Exception()
                    cont.resumeWithException(error)
                }
            }
        }
    }

    private fun onTtsStatusUpdate(utteranceId: Int, newStatus: TextToSpeechInstance.Status = TextToSpeechInstance.Status.FINISHED) {
        callbacks[utteranceId]?.invoke(Result.success(newStatus))
    }

    private fun onTtsStatusUpdate(utteranceId: Int, error: Throwable) {
        callbacks[utteranceId]?.invoke(Result.failure(error))
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun plusAssign(text: String) = enqueue(text, false)

    /** Clears the internal queue, but doesn't close used resources. */
    override fun stop() {
        tts?.stop()
    }

    /** Clears the internal queue and closes used resources (if possible) */
    override fun close() {
        stop()
        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            tts?.setOnUtteranceProgressListener(null)
        } else {
            tts?.setOnUtteranceCompletedListener(null)
        }
        tts?.shutdown()
        tts = null
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
    }
}
