@file:Suppress("unused")

package nl.marc_apps.tts

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.speech.tts.UtteranceProgressListener
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import android.speech.tts.TextToSpeech as AndroidTTS

/** A TTS instance. Should be [close]d when no longer in use. */
@TargetApi(VERSION_CODES.DONUT)
internal class TextToSpeechAndroid(private var tts: AndroidTTS?) : TextToSpeechInstance, UtteranceProgressListener(), AndroidTTS.OnUtteranceCompletedListener {
    private val callbacks = mutableMapOf<Int, (Result<TextToSpeechInstance.Status>) -> Unit>()

    private val internalVolume: Float
        get() = if(!isMuted) volume / 100f else 0f

    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     * Changes only affect new calls to the [say] method.
     */
    override var volume: Int = 100
        set(value) {
            if(TextToSpeech.canChangeVolume)
                field = value
        }

    /**
     * Alternative to setting [volume] to zero.
     * Setting this to true (and back to false) doesn't change the value of [volume].
     * Changes only affect new calls to the [say] method.
     */
    override var isMuted: Boolean = false

    override var pitch: Float = 1f
        set(value) {
            field = value
            tts?.setPitch(value)
        }

    override var rate: Float = 1f
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
        tts?.setOnUtteranceProgressListener(this)
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
            return
        }

        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        val utteranceId = Objects.hash(System.currentTimeMillis(), text)
        callbacks += utteranceId to callback
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
                if (it.isSuccess && it.getOrNull() == resumeOnStatus) cont.resume(Unit)
                else if (it.isFailure) it.exceptionOrNull()?.let { thr -> cont.resumeWithException(thr) }
            }
        }
    }

    override fun onStart(utteranceId: String?) {
        callbacks[utteranceId?.toInt() ?: -1]?.invoke(Result.success(TextToSpeechInstance.Status.STARTED))
    }

    override fun onDone(utteranceId: String?) {
        callbacks[utteranceId?.toInt() ?: -1]?.invoke(Result.success(TextToSpeechInstance.Status.FINISHED))
    }

    override fun onUtteranceCompleted(utteranceId: String?) = onDone(utteranceId)

    override fun onError(utteranceId: String?) {
        callbacks[utteranceId?.toInt() ?: -1]?.invoke(Result.failure(Exception()))
    }

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    override fun plusAssign(text: String) = enqueue(text, false)

    /** Clears the internal queue, but doesn't close used resources. */
    override fun stop() {
        tts?.stop()
    }

    /** Clears the internal queue and closes used resources (if possible) */
    override fun close() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }

    companion object {
        private const val KEY_PARAM_VOLUME = "volume"
        private const val KEY_PARAM_UTTERANCE_ID = "utteranceId"
    }
}
