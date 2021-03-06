package nl.marc.tts

import kotlinx.browser.window
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Functions and properties that can be used to create new TTS instances
 * and check for compatibility issues.
 */
actual object TextToSpeech {
    actual val isSupported = js("\"speechSynthesis\" in window") as Boolean

    actual val canChangeVolume = true

    /**
     * Creates a new [TextToSpeechInstance].
     * @throws TextToSpeechNotSupportedError when TTS is not supported.
     */
    fun createOrThrowSync(context: Context = window): TextToSpeechInstance {
        if(isSupported) return TextToSpeechJS(context)
        else throw TextToSpeechNotSupportedError
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * Will return null if TTS is not supported.
     */
    fun createOrNullSync(context: Context = window): TextToSpeechInstance? {
        return if(isSupported) TextToSpeechJS(context) else null
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * Will call [callback] with null if TTS is not supported.
     */
    actual fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit) {
        callback(createOrNullSync(context))
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * Will do nothing and will not execute [callback] when TTS is not supported.
     */
    actual fun createOrNothing(context: Context, callback: (TextToSpeechInstance) -> Unit) {
        if(isSupported) callback(TextToSpeechJS(context))
    }

    /** Creates a new [TextToSpeechInstance]. */
    actual fun create(context: Context, callback: (Result<TextToSpeechInstance>) -> Unit) {
        if(isSupported) callback(Result.success(TextToSpeechJS(context)))
        else callback(Result.failure(TextToSpeechNotSupportedError))
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * @throws TextToSpeechNotSupportedError when TTS is not supported.
     */
    actual suspend fun createOrThrow(context: Context): TextToSpeechInstance = suspendCoroutine { cont ->
        create(context) {
            cont.resumeWith(it)
        }
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * Will return null if TTS is not supported.
     */
    actual suspend fun createOrNull(context: Context): TextToSpeechInstance? = suspendCoroutine { cont ->
        createOrNull(context) {
            cont.resume(it)
        }
    }
}
