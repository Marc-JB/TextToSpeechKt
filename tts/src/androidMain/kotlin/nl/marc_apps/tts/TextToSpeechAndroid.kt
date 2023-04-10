@file:Suppress("unused")

package nl.marc_apps.tts

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import nl.marc_apps.tts.errors.*
import nl.marc_apps.tts.utils.TtsProgressConverter
import nl.marc_apps.tts.utils.getContinuationId
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.speech.tts.TextToSpeech as AndroidTTS

/** A TTS instance. Should be [close]d when no longer in use. */
@TargetApi(VERSION_CODES.DONUT)
internal class TextToSpeechAndroid(private var tts: AndroidTTS?) : TextToSpeechInstance {
    private val callbacks = mutableMapOf<UUID, (Result<TextToSpeechInstance.Status>) -> Unit>()

    override val isSynthesizing = MutableStateFlow(false)

    private var preIcsQueueSize = 0

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
            val id = getContinuationId(it) ?: return@setOnUtteranceCompletedListener
            onTtsStatusUpdate(id, Result.success(TextToSpeechInstance.Status.FINISHED))
        }
    }

    @RequiresApi(VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private fun setProgressListeners() {
        tts?.setOnUtteranceProgressListener(TtsProgressConverter(::onTtsStatusUpdate))
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun enqueue(text: String, clearQueue: Boolean) {
        say(text, clearQueue) {}
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun say(text: String, clearQueue: Boolean, callback: (Result<TextToSpeechInstance.Status>) -> Unit) {
        if(isMuted || internalVolume == 0f) {
            if(clearQueue) stop()
            callback(Result.success(TextToSpeechInstance.Status.FINISHED))
            return
        }

        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        val utteranceId = UUID.randomUUID()

        callbacks += utteranceId to {
            callback(it)

            if (it.isFailure || it.getOrNull() == TextToSpeechInstance.Status.FINISHED) {
                callbacks.remove(utteranceId)
            }
        }

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val params = Bundle()
            params.putFloat(KEY_PARAM_VOLUME, internalVolume)
            params.putString(KEY_PARAM_UTTERANCE_ID, utteranceId.toString())
            tts?.speak(text, queueMode, params, utteranceId.toString())
        } else {
            val params = hashMapOf(
                KEY_PARAM_VOLUME to internalVolume.toString(),
                KEY_PARAM_UTTERANCE_ID to utteranceId.toString()
            )

            if (VERSION.SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                preIcsQueueSize++
                isSynthesizing.value = true
            }

            tts?.speak(text, queueMode, params)
        }
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override suspend fun say(text: String, clearQueue: Boolean, resumeOnStatus: TextToSpeechInstance.Status) {
        suspendCoroutine { cont ->
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

    private fun onTtsStatusUpdate(utteranceId: UUID, result: Result<TextToSpeechInstance.Status>) {
        if (VERSION.SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            preIcsQueueSize--
            if (preIcsQueueSize == 0)
            {
                isSynthesizing.value = false
            }
        } else {
            isSynthesizing.value = result.getOrNull() == TextToSpeechInstance.Status.STARTED
        }

        callbacks[utteranceId]?.invoke(result)
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun plusAssign(text: String) = enqueue(text, false)

    /** Clears the internal queue, but doesn't close used resources. */
    override fun stop() {
        tts?.stop()

        if (VERSION.SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            preIcsQueueSize = 0
            isSynthesizing.value = false
        }
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
    }
}
