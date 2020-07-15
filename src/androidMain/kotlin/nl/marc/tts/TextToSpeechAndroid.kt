@file:Suppress("unused")

package nl.marc.tts

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import java.io.Closeable
import android.speech.tts.TextToSpeech as AndroidTTS

@TargetApi(VERSION_CODES.DONUT)
internal class TextToSpeechAndroid(private val tts: AndroidTTS) : TextToSpeechInstance {
    override var volume: Int = 100
        set(value) {
            if(TextToSpeech.canChangeVolume)
                field = value
        }

    override fun say(text: String, clearQueue: Boolean) {
        val queueMode = if(clearQueue) AndroidTTS.QUEUE_FLUSH else AndroidTTS.QUEUE_ADD
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val params = Bundle()
            params.putFloat(KEY_PARAM_VOLUME, volume / 100f)
            tts.speak(text, queueMode, params, null)
        } else {
            val params = hashMapOf(
                KEY_PARAM_VOLUME to (volume / 100f).toString()
            )
            tts.speak(text, queueMode, params)
        }
    }

    override fun close() {
        tts.stop()
        tts.shutdown()
    }

    companion object {
        const val KEY_PARAM_VOLUME = "volume"
    }
}

