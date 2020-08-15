package nl.marc.tts

import nl.marc.tts.errors.TextToSpeechNotSupportedError
import nl.marc.tts.errors.Throws
import kotlin.browser.window

/**
 * Functions and properties that can be used to create new TTS instances
 * and check for compatibility issues.
 */
@JsExport
actual object TextToSpeech {
    actual val isSupported = js("\"speechSynthesis\" in window") as Boolean

    actual val canChangeVolume = true

    /**
     * Creates a new [TextToSpeechInstance].
     * @throws TextToSpeechNotSupportedError when TTS is not supported.
     */
    @Throws(TextToSpeechNotSupportedError::class)
    fun createOrThrow(context: Context = window): TextToSpeechInstance {
        if(isSupported) return TextToSpeechJS(context)
        else throw TextToSpeechNotSupportedError
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * Will return null if TTS is not supported.
     */
    fun createOrNull(context: Context = window): TextToSpeechInstance? {
        return if(isSupported) TextToSpeechJS(context) else null
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * @throws TextToSpeechNotSupportedError when TTS is not supported.
     */
    @Deprecated("Use TextToSpeech.create(ctx, cb)")
    @Throws(TextToSpeechNotSupportedError::class)
    actual fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit) {
        if(isSupported) callback(TextToSpeechJS(context))
        else throw TextToSpeechNotSupportedError
    }

    /**
     * Creates a new [TextToSpeechInstance].
     * Will call [callback] with null if TTS is not supported.
     */
    actual fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit) {
        callback(createOrNull(context))
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
}
