@file:Suppress("unused")

package nl.marc_apps.tts

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.marc_apps.tts.utils.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.speech.tts.TextToSpeech as AndroidTTS

/** A TTS instance. Should be [close]d when no longer in use. */
@TargetApi(VERSION_CODES.DONUT)
internal class TextToSpeechAndroid(private var tts: AndroidTTS?) {
    private val callbacks = mutableMapOf<UUID, (Result<Unit>) -> Unit>()

    val isSynthesizing = MutableStateFlow(false)

    val isWarmingUp = MutableStateFlow(false)

    private var hasSpoken = false

    private var preIcsQueueSize = 0

    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
     * Changes only affect new calls to the [say] method.
     */
    @IntRange(from = TextToSpeechInstance.VOLUME_MIN.toLong(), to = TextToSpeechInstance.VOLUME_MAX.toLong())
    var volume: Int = TextToSpeechInstance.VOLUME_DEFAULT
        set(value) {
            if(TextToSpeechFactory.canChangeVolume) {
                field = when {
                    value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                    value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                    else -> value
                }
            }
        }

    var pitch: Float = TextToSpeechInstance.VOICE_PITCH_DEFAULT
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    var rate: Float = TextToSpeechInstance.VOICE_RATE_DEFAULT
        set(value) {
            field = value
            tts?.setSpeechRate(value)
        }

    private val voiceLocale: Locale
        get() = (if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) tts?.voice?.locale else tts?.language) ?: Locale.getDefault()

    private val defaultVoice: Voice? = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP){
        (tts?.voice ?: tts?.defaultVoice)?.let { VoiceAndroidModern(it, true) }
    } else {
        tts?.language?.let { VoiceAndroidLegacy(it, true) }
    }

    var currentVoice: Voice? = defaultVoice
        set(value) {
            val result = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && value is VoiceAndroidModern) {
                tts?.setVoice(value.androidVoice)
            } else if (value is VoiceAndroidLegacy) {
                tts?.setLanguage(value.locale)
            } else null
            if (result != null && result >= 0) {
                field = value
            }
        }

    val voices: Sequence<Voice> = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
        (tts?.voices ?: emptySet()).asSequence().map {
            VoiceAndroidModern(it, it == (defaultVoice as? VoiceAndroidModern)?.androidVoice)
        }
    } else {
        Locale.getAvailableLocales().asSequence().filter {
            tts?.isLanguageAvailable(it) == AndroidTTS.LANG_AVAILABLE
        }.map {
            VoiceAndroidLegacy(it, it == defaultVoice?.locale)
        }
    }

    init {
        if (hasModernProgressListeners) {
            setProgressListeners()
        } else {
            setProgressListenersLegacy()
        }
    }

    private fun setProgressListenersLegacy() {
        tts?.setOnUtteranceCompletedListener {
            val id = getContinuationId(it) ?: return@setOnUtteranceCompletedListener
            onTtsCompleted(id, Result.success(Unit))
        }
    }

    @RequiresApi(VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun setProgressListeners() {
        tts?.setOnUtteranceProgressListener(TtsProgressConverter(::onTtsStarted, ::onTtsCompleted))
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun enqueue(text: String, clearQueue: Boolean) {
        say(text, clearQueue) {}
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        if(volume == 0) {
            if(clearQueue) stop()
            callback(Result.success(Unit))
            return
        }

        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        val utteranceId = UUID.randomUUID()

        callbacks += utteranceId to {
            callback(it)
            callbacks.remove(utteranceId)
        }

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && !hasSpoken) {
            hasSpoken = true
            isWarmingUp.value = true
        }

        val params = Bundle().apply {
            putFloat(KEY_PARAM_VOLUME, volume / 100f)
            putString(KEY_PARAM_UTTERANCE_ID, utteranceId.toString())
        }

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            tts?.speak(text, queueMode, params, utteranceId.toString())
        } else {
            if (!hasModernProgressListeners) {
                preIcsQueueSize++
                isSynthesizing.value = true
            }

            tts?.speak(text, queueMode, HashMap(params.toMap().mapValues { it.toString() }))
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean){
        suspendCancellableCoroutine { cont ->
            say(text, clearQueue) {
                if (it.isSuccess) {
                    cont.resume(Unit)
                } else if (it.isFailure) {
                    val error = it.exceptionOrNull() ?: Exception()
                    cont.resumeWithException(error)
                }
            }
            cont.invokeOnCancellation {
                if (clearQueueOnCancellation) {
                    stop()
                }
            } 
        }
    }

    private fun onTtsStarted(utteranceId: UUID) {
        isWarmingUp.value = false
        isSynthesizing.value = true
    }

    private fun onTtsCompleted(utteranceId: UUID, result: Result<Unit>) {
        isWarmingUp.value = false

        if (!hasModernProgressListeners) {
            preIcsQueueSize--
            if (preIcsQueueSize == 0)
            {
                isSynthesizing.value = false
            }
        } else {
            isSynthesizing.value = false
        }

        callbacks[utteranceId]?.invoke(result)
    }

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop() {
        tts?.stop()

        if (!hasModernProgressListeners) {
            preIcsQueueSize = 0
            isSynthesizing.value = false
        }
    }

    /** Clears the internal queue and closes used resources (if possible) */
    fun close() {
        stop()
        if (hasModernProgressListeners) {
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

        @ChecksSdkIntAtLeast(api = VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        private val hasModernProgressListeners = VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1
    }
}
