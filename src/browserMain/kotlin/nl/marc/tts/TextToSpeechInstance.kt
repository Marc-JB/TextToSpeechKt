package nl.marc.tts

@JsExport
actual interface TextToSpeechInstance {
    actual var volume: Int

    actual fun say(text: String, clearQueue: Boolean)

    actual fun close()
}
