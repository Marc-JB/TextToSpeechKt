package nl.marc.tts

import java.io.Closeable

actual interface TextToSpeechInstance : Closeable {
    actual var volume: Int

    actual fun say(text: String, clearQueue: Boolean)

    @Throws(Exception::class)
    actual override fun close()
}
