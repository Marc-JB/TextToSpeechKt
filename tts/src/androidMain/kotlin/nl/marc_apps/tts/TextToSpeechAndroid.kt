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
import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import nl.marc_apps.tts.utils.CallbackHandler
import nl.marc_apps.tts.utils.ResultHandler
import nl.marc_apps.tts.utils.TtsProgressConverter
import nl.marc_apps.tts.utils.VoiceAndroidLegacy
import nl.marc_apps.tts.utils.VoiceAndroidModern
import nl.marc_apps.tts.utils.getContinuationId
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import android.speech.tts.TextToSpeech as AndroidTTS

@OptIn(ExperimentalUuidApi::class)
@TargetApi(VERSION_CODES.DONUT)
internal class TextToSpeechAndroid(private var tts: AndroidTTS?) : TextToSpeechInstance {
    private val callbackHandler = CallbackHandler<String>()

    override val isSynthesizing = MutableStateFlow(false)

    override val isWarmingUp = MutableStateFlow(false)

    private var hasSpoken = false

    private val internalVolume: Float
        get() = if(!isMuted) volume / 100f else 0f

    @IntRange(from = TextToSpeechInstance.VOLUME_MIN.toLong(), to = TextToSpeechInstance.VOLUME_MAX.toLong())
    override var volume: Int = TextToSpeechInstance.VOLUME_DEFAULT
        set(value) {
            if(TextToSpeechFactory.canChangeVolume) {
                field = when {
                    value < TextToSpeechInstance.VOLUME_MIN -> TextToSpeechInstance.VOLUME_MIN
                    value > TextToSpeechInstance.VOLUME_MAX -> TextToSpeechInstance.VOLUME_MAX
                    else -> value
                }
            }
        }

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

    override val language: String
        get() = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) voiceLocale.toLanguageTag() else voiceLocale.language

    @ExperimentalVoiceApi
    private val defaultVoice: Voice? = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP){
        (tts?.voice ?: tts?.defaultVoice)?.let { VoiceAndroidModern(it, true) }
    } else {
        tts?.language?.let { VoiceAndroidLegacy(it, true) }
    }

    @ExperimentalVoiceApi
    override var currentVoice: Voice? = defaultVoice
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

    @ExperimentalVoiceApi
    override val voices: Sequence<Voice> = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
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

    override fun enqueue(text: String, clearQueue: Boolean) {
        enqueueInternal(text, clearQueue, ResultHandler.Empty)
    }

    override fun say(text: String, clearQueue: Boolean, callback: (Result<Unit>) -> Unit) {
        enqueueInternal(text, clearQueue, ResultHandler.CallbackHandler(callback))
    }

    private fun enqueueInternal(text: String, clearQueue: Boolean, resultHandler: ResultHandler) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            resultHandler.setResult(Result.success(Unit))
            return
        }

        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        val utteranceId = Uuid.random()
        val utteranceIdString = utteranceId.toString()

        callbackHandler.add(utteranceId, utteranceIdString, resultHandler)

        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && !hasSpoken) {
            hasSpoken = true
            isWarmingUp.value = true
        }

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val params = Bundle()
            params.putFloat(KEY_PARAM_VOLUME, internalVolume)
            params.putString(KEY_PARAM_UTTERANCE_ID, utteranceIdString)
            tts?.speak(text, queueMode, params, utteranceIdString)
        } else {
            val params = hashMapOf(
                KEY_PARAM_VOLUME to internalVolume.toString(),
                KEY_PARAM_UTTERANCE_ID to utteranceIdString
            )

            if (!hasModernProgressListeners) {
                isSynthesizing.value = true
            }

            tts?.speak(text, queueMode, params)
        }
    }

    override suspend fun say(text: String, clearQueue: Boolean, clearQueueOnCancellation: Boolean){
        suspendCancellableCoroutine { cont ->
            enqueueInternal(text, clearQueue, ResultHandler.ContinuationHandler(cont))
            cont.invokeOnCancellation {
                if (clearQueueOnCancellation) {
                    stop()
                }
            } 
        }
    }

    private fun onTtsStarted(utteranceId: Uuid) {
        isWarmingUp.value = false
        isSynthesizing.value = true
    }

    private fun onTtsCompleted(utteranceId: Uuid, result: Result<Unit>) {
        isWarmingUp.value = false

        callbackHandler.onResult(utteranceId, result)

        if (!hasModernProgressListeners) {
            if (callbackHandler.isQueueEmpty)
            {
                isSynthesizing.value = false
            }
        } else {
            isSynthesizing.value = false
        }
    }

    override fun plusAssign(text: String) = enqueue(text, false)

    override fun stop() {
        tts?.stop()

        callbackHandler.onStopped()
        if (!hasModernProgressListeners) {
            isSynthesizing.value = false
        }
    }

    override fun close() {
        stop()
        if (hasModernProgressListeners) {
            tts?.setOnUtteranceProgressListener(null)
        } else {
            tts?.setOnUtteranceCompletedListener(null)
        }
        tts?.shutdown()
        tts = null
        callbackHandler.clear()
    }

    companion object {
        private const val KEY_PARAM_VOLUME = "volume"

        private const val KEY_PARAM_UTTERANCE_ID = "utteranceId"

        @ChecksSdkIntAtLeast(api = VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        private val hasModernProgressListeners = VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1
    }
}
