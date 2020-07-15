package nl.marc.tts

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.speech.tts.TextToSpeech
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Throws

actual object TextToSpeech {
    actual val isSupported: Boolean = VERSION.SDK_INT >= VERSION_CODES.DONUT

    actual val canChangeVolume = VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB

    @TargetApi(VERSION_CODES.DONUT)
    private suspend fun createAndroidTTS(context: Context): Pair<TextToSpeech, Int> = suspendCoroutine { cont ->
        lateinit var obj: TextToSpeech
        obj = TextToSpeech(context) {
            cont.resume(obj to it)
        }
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will throw an [TextToSpeechNotSupportedException] if TTS is not supported.
     */
    @Throws(TextToSpeechNotSupportedException::class)
    suspend fun createOrThrow(context: Context): TextToSpeechInstance {
        if(!isSupported) throw TextToSpeechNotSupportedException()

        val (tts, code) = createAndroidTTS(context)

        if (code == TextToSpeech.SUCCESS) return TextToSpeechAndroid(tts)
        else throw TextToSpeechNotSupportedException()
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will return null if TTS is not supported.
     */
    suspend fun createOrNull(context: Context): TextToSpeechInstance? {
        if(!isSupported) return null
        val (tts, code) = createAndroidTTS(context)
        return if (code == TextToSpeech.SUCCESS) TextToSpeechAndroid(tts) else null
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will throw an [TextToSpeechNotSupportedException] if TTS is not supported.
     */
    @Throws(TextToSpeechNotSupportedException::class)
    actual fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit) {
        if(!isSupported) {
            throw TextToSpeechNotSupportedException()
        } else if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
            lateinit var obj: TextToSpeech
            obj = TextToSpeech(context) {
                if(it == TextToSpeech.SUCCESS) callback(TextToSpeechAndroid(obj))
                else throw TextToSpeechNotSupportedException()
            }
        }
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will return null if TTS is not supported.
     */
    actual fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit) {
        if(!isSupported) {
            callback(null)
        } else if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
            lateinit var obj: TextToSpeech
            obj = TextToSpeech(context) {
                callback(if(it == TextToSpeech.SUCCESS) TextToSpeechAndroid(obj) else null)
            }
        }
    }
}
