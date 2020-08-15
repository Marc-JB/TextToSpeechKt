package nl.marc.tts

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.speech.tts.TextToSpeech
import nl.marc.tts.errors.TextToSpeechInitialisationError
import nl.marc.tts.errors.TextToSpeechNotSupportedError
import nl.marc.tts.errors.TextToSpeechSecurityError
import nl.marc.tts.errors.UnknownTextToSpeechError
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

    private inline fun createAndroidTTS(context: Context, crossinline callback: (Result<TextToSpeech>) -> Unit) {
        if (VERSION.SDK_INT >= VERSION_CODES.DONUT) {
            lateinit var obj: TextToSpeech
            try {
                obj = TextToSpeech(context) {
                    if (it == TextToSpeech.SUCCESS) callback(Result.success(obj))
                    else callback(Result.failure(UnknownTextToSpeechError))
                }
            } catch (e: SecurityException) {
                callback(Result.failure(TextToSpeechSecurityError))
            }
        } else callback(Result.failure(TextToSpeechNotSupportedError))
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * @throws TextToSpeechInitialisationError
     */
    @Throws(TextToSpeechInitialisationError::class)
    suspend fun createOrThrow(context: Context): TextToSpeechInstance = suspendCoroutine { cont ->
        create(context) {
            cont.resumeWith(it)
        }
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will return null if TTS is not supported.
     */
    suspend fun createOrNull(context: Context): TextToSpeechInstance? = suspendCoroutine { cont ->
        createOrNull(context) {
            cont.resume(it)
        }
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * @throws TextToSpeechInitialisationError
     */
    @Throws(TextToSpeechInitialisationError::class)
    actual fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit) {
        createAndroidTTS(context) {
            if(it.isSuccess) callback(TextToSpeechAndroid(it.getOrNull()))
            else throw it.exceptionOrNull() ?: UnknownTextToSpeechError
        }
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will call [callback] with null if TTS is not supported.
     */
    actual fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit) {
        createAndroidTTS(context) {
            callback(if(it.isSuccess) TextToSpeechAndroid(it.getOrNull()) else null)
        }
    }

    /** Creates a new [TextToSpeech] instance. */
    actual fun create(context: Context, callback: (Result<TextToSpeechInstance>) -> Unit) {
        createAndroidTTS(context) {
            if(it.isSuccess) callback(Result.success(TextToSpeechAndroid(it.getOrNull())))
            else callback(Result.failure(it.exceptionOrNull() ?: UnknownTextToSpeechError))
        }
    }
}
