package nl.marc_apps.tts

/** Error that is thrown when an error occurs while creating a [nl.marc.tts.TextToSpeechInstance]. */
sealed class TextToSpeechInitialisationError(
    message: String? = "Error while trying to load Text-to-Speech service",
    cause: Throwable? = null
) : Exception(message, cause)

/** Error that is thrown when a platform does not have TTS support */
object TextToSpeechNotSupportedError : TextToSpeechInitialisationError("Text-to-Speech is not supported on this platform")

/** Error that is thrown when an error occurs while creating a [nl.marc.tts.TextToSpeechInstance]. */
object UnknownTextToSpeechError : TextToSpeechInitialisationError()

/** Error that is thrown on some Samsung devices while creating a TTS instance. */
object TextToSpeechSecurityError : TextToSpeechInitialisationError("Text-to-Speech security error")
