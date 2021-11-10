package nl.marc_apps.tts

/**
 * Use from JavaScript/TypeScript code only. Use the [TextToSpeechInstance] interface from Kotlin code.
 * A TTS instance. Should be [close]d when no longer in use.
 */
@ExperimentalJsExport
@JsExport
open class TextToSpeechInstanceJS internal constructor() {
    /**
     * The output volume, which is 100(%) by default.
     * Value is minimally 0, maximally 100 (although some platforms may allow higher values).
     * Changes only affect new calls to the [enqueue] method.
     */
    open var volume: Int = 100

    /**
     * Alternative to setting [volume] to zero.
     * Setting this to true (and back to false) doesn't change the value of [volume].
     * Changes only affect new calls to the [enqueue] method.
     */
    open var isMuted: Boolean = false

    open var pitch: Float = 1f

    open var rate: Float = 1f

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    open val language: String = "en-US"

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    open fun enqueue(text: String, clearQueue: Boolean) {
        throw Exception()
    }

    /** Clears the internal queue, but doesn't close used resources. */
    open fun stop() {
        throw Exception()
    }

    /** Clears the internal queue and closes used resources (if possible) */
    open fun close() {
        throw Exception()
    }
}
