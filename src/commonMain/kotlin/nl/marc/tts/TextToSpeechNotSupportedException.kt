package nl.marc.tts

/** Exception that is thrown when a platform does not have TTS support */
class TextToSpeechNotSupportedException : Exception("TTS is not supported on this platform")
