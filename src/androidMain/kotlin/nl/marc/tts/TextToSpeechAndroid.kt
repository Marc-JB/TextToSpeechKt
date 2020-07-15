@file:Suppress("unused")

package nl.marc.tts

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import java.io.Closeable
import java.util.*
import android.speech.tts.TextToSpeech as AndroidTTS

@TargetApi(VERSION_CODES.DONUT)
internal class TextToSpeechAndroid(private val tts: AndroidTTS) : TextToSpeechInstance {
    private val internalVolume: Float
        get() = if(!isMuted) volume / 100f else 0f

    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     */
    override var volume: Int = 100
        set(value) {
            if(TextToSpeech.canChangeVolume)
                field = value
        }

    override var isMuted: Boolean = false
        set(value) {
            if(TextToSpeech.canChangeVolume)
                field = value
        }

    override var pitch: Float = 1f
        set(value) {
            field = value
            tts.setPitch(value)
        }

    override var rate: Float = 1f
        set(value) {
            field = value
            tts.setSpeechRate(value)
        }

    private val voiceLocale: Locale
        get() = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) tts.voice.locale else tts.language

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    override val language: String
        get() = if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) voiceLocale.toLanguageTag() else voiceLocale.language

    override fun say(text: String, clearQueue: Boolean) {
        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val params = Bundle()
            params.putFloat(KEY_PARAM_VOLUME, internalVolume)
            tts.speak(text, queueMode, params, null)
        } else {
            val params = hashMapOf(
                KEY_PARAM_VOLUME to internalVolume.toString()
            )
            tts.speak(text, queueMode, params)
        }
    }

    /** Clears the internal queue, but doesn't close used resources. */
    override fun stop() {
        tts.stop()
    }

    /** Clears the internal queue and closes used resources (if possible) */
    override fun close() {
        tts.stop()
        tts.shutdown()
    }

    companion object {
        private const val KEY_PARAM_VOLUME = "volume"
    }
}

