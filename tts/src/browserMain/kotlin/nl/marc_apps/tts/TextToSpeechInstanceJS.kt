package nl.marc_apps.tts

/**
 * Use from JavaScript/TypeScript code only. Use the [TextToSpeechInstance] interface from Kotlin code.
 * A TTS instance. Should be [close]d when no longer in use.
 */
@ExperimentalJsExport
@JsExport
class TextToSpeechInstanceJS internal constructor(private val ttsInstance: TextToSpeechInstance) {
    /**
     * The output volume, which is an integer between 0 and 100, set to 100(%) by default.
     * Changes only affect new calls to the [enqueue] method.
     */
    var volume: Int by ttsInstance::volume

    /**
     * Alternative to setting [volume] to zero.
     * Setting this to true (and back to false) doesn't change the value of [volume].
     * Changes only affect new calls to the [enqueue] method.
     */
    var isMuted: Boolean by ttsInstance::isMuted

    var pitch: Float by ttsInstance::pitch

    var rate: Float by ttsInstance::rate

    /**
     * Returns a BCP 47 language tag of the selected voice on supported platforms.
     * May return the language code as ISO 639 on older platforms.
     */
    val language: String by ttsInstance::language

    /** Adds the given [text] to the internal queue, unless [isMuted] is true or [volume] equals 0. */
    fun enqueue(text: String, clearQueue: Boolean = false) = ttsInstance.enqueue(text, clearQueue)

    /** Clears the internal queue, but doesn't close used resources. */
    fun stop() = ttsInstance.stop()

    /** Clears the internal queue and closes used resources (if possible) */
    fun close() = ttsInstance.close()
}
