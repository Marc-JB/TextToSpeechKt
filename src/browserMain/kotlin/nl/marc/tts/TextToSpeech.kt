package nl.marc.tts

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
     * Creates a new [TextToSpeech] instance.
     * @throws TextToSpeechNotSupportedException when TTS is not supported.
     */
    @Throws(TextToSpeechNotSupportedException::class)
    fun createOrThrow(context: Context = window): TextToSpeechInstance {
        if(isSupported) return TextToSpeechJS(context)
        else throw TextToSpeechNotSupportedException()
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will return null if TTS is not supported.
     */
    fun createOrNull(context: Context = window): TextToSpeechInstance? {
        return if(isSupported) TextToSpeechJS(context) else null
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * @throws TextToSpeechNotSupportedException when TTS is not supported.
     */
    @Throws(TextToSpeechNotSupportedException::class)
    actual fun createOrThrow(context: Context, callback: (TextToSpeechInstance) -> Unit) {
        if(isSupported) callback(TextToSpeechJS(context))
        else throw TextToSpeechNotSupportedException()
    }

    /**
     * Creates a new [TextToSpeech] instance.
     * Will call [callback] with null if TTS is not supported.
     */
    actual fun createOrNull(context: Context, callback: (TextToSpeechInstance?) -> Unit) {
        callback(createOrNull(context))
    }
}
