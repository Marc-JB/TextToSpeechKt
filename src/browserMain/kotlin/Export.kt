import nl.marc.tts.TextToSpeech
import nl.marc.tts.TextToSpeechInstance
import nl.marc.tts.TextToSpeechNotSupportedError

@JsExport
val isSupported = TextToSpeech.isSupported

@JsExport
val canChangeVolume = TextToSpeech.canChangeVolume

/**
 * Creates a new [TextToSpeechInstance].
 * @throws TextToSpeechNotSupportedError when TTS is not supported.
 */
@JsExport
fun createOrThrow() = TextToSpeech.createOrThrowSync()

/**
 * Creates a new [TextToSpeechInstance].
 * Will return null if TTS is not supported.
 */
@JsExport
fun createOrNull() = TextToSpeech.createOrNullSync()

