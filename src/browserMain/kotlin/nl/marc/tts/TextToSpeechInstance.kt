package nl.marc.tts

@JsExport
actual interface TextToSpeechInstance {
    actual var volume: Int

    actual var isMuted: Boolean

    actual fun say(text: String, clearQueue: Boolean)

    actual fun stop()

    actual fun close()
}
