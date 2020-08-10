package nl.marc.tts

import android.annotation.TargetApi
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.speech.tts.TextToSpeech
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Throws

/**
 * Functions and properties that can be used to create new TTS instances
 * and check for compatibility issues.
 */
actual object TextToSpeech {
    actual val isSupported: Boolean = VERSION.SDK_INT >= VERSION_CODES.DONUT

    actual val canChangeVolume = VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB

    @TargetApi(VERSION_CODES.DONUT)
    private inline fun createAndroidTTS(context: Context, crossinline callback: (TextToSpeech?, Int) -> Unit) {
        lateinit var obj: TextToSpeech
        try {
            obj = TextToSpeech(context) {
                callback(obj, it)
            }
        } catch (e: SecurityException) {
            callback(null, TextToSpeech.ERROR)
        }
    }

    @TargetApi(VERSION_CODES.DONUT)
    private suspend inline fun createAndroidTTS(context: Context): Pair<TextToSpeech?, Int> = suspendCoroutine { cont ->
        createAndroidTTS(context) { tts, code ->
            cont.resume(tts to code)
        }
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * @throws TextToSpeechNotSupportedException when TTS is not supported.
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
     * @throws TextToSpeechNotSupportedException when TTS is not supported.
     */
    @Throws(TextToSpeechNotSupportedException::class)
    actual fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit) {
        if(!isSupported) {
            throw TextToSpeechNotSupportedException()
        } else if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
            createAndroidTTS(context) { tts, code ->
                if(code == TextToSpeech.SUCCESS) callback(TextToSpeechAndroid(tts))
                else throw TextToSpeechNotSupportedException()
            }
        }
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will call [callback] with null if TTS is not supported.
     */
    actual fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit) {
        if(!isSupported) {
            callback(null)
        } else if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
            createAndroidTTS(context) { tts, code ->
                callback(if(code == TextToSpeech.SUCCESS) TextToSpeechAndroid(tts) else null)
                if(code == TextToSpeech.SUCCESS) callback(TextToSpeechAndroid(tts))
                else throw TextToSpeechNotSupportedException()
            }
        }
    }
}
