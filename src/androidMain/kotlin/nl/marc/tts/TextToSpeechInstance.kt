package nl.marc.tts

import java.io.Closeable

actual interface TextToSpeechInstance : Closeable {
    actual var volume: Int

    actual fun say(text: String, clearQueue: Boolean)

    actual override fun close()
}
