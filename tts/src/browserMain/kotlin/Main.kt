import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.TextToSpeechInstanceJS
import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError

/**
 * Use from JavaScript/TypeScript code only. Use the [TextToSpeech] object from Kotlin code.
 */
@ExperimentalJsExport
@JsExport
val isTtsSupported = TextToSpeech.isSupported

/**
 * Use from JavaScript/TypeScript code only. Use the [TextToSpeech] object from Kotlin code.
 */
@ExperimentalJsExport
@JsExport
val canChangeTtsVolume = TextToSpeech.canChangeVolume

/**
 * Use from JavaScript/TypeScript code only. Use the [TextToSpeech] object from Kotlin code.
 * Creates a new [TextToSpeechInstance].
 * @throws TextToSpeechNotSupportedError when TTS is not supported.
 */
@ExperimentalJsExport
@JsExport
fun createTtsOrThrow() = TextToSpeechInstanceJS(TextToSpeech.createOrThrowSync())

/**
 * Use from JavaScript/TypeScript code only. Use the [TextToSpeech] object from Kotlin code.
 * Creates a new [TextToSpeechInstance].
 * Will return null if TTS is not supported.
 */
@ExperimentalJsExport
@JsExport
fun createTtsOrNull() = TextToSpeech.createOrNullSync()?.let { TextToSpeechInstanceJS(it) }
