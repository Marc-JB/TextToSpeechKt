import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.TextToSpeechJS
import nl.marc_apps.tts.TextToSpeechNotSupportedError

@JsExport
val isSupported = TextToSpeech.isSupported

@JsExport
val canChangeVolume = TextToSpeech.canChangeVolume

/**
 * Creates a new [TextToSpeechInstance].
 * @throws TextToSpeechNotSupportedError when TTS is not supported.
 */
@JsExport
fun createOrThrow() = TextToSpeech.createOrThrowSync() as TextToSpeechJS

/**
 * Creates a new [TextToSpeechInstance].
 * Will return null if TTS is not supported.
 */
@JsExport
fun createOrNull() = TextToSpeech.createOrNullSync() as? TextToSpeechJS?
