import nl.marc.tts.TextToSpeech
import nl.marc.tts.TextToSpeechNotSupportedException

@JsExport
val isSupported = TextToSpeech.isSupported

@JsExport
val canChangeVolume = TextToSpeech.canChangeVolume

/**
 * Creates a new [TextToSpeech] instance.
 * Will throw an [TextToSpeechNotSupportedException] if TTS is not supported.
 */
@JsExport
fun createOrThrow() = TextToSpeech.createOrThrow()

/**
 * Creates a new [TextToSpeech] instance.
 * Will return null if TTS is not supported.
 */
@JsExport
fun createOrNull() = TextToSpeech.createOrNull()
