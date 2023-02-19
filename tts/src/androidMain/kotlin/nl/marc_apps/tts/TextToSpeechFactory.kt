package nl.marc_apps.tts

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.ChecksSdkIntAtLeast
import nl.marc_apps.tts.implementation.lollipop.TextToSpeechInstanceAndroidModern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class TextToSpeechFactory(private val context: Context) {
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.DONUT)
    actual val isSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT

    @Throws(RuntimeException::class)
    actual suspend fun create(): TextToSpeechInstance {
        TODO("Not yet implemented")
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return suspendCoroutine { cont ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                lateinit var obj: TextToSpeech
                try {
                    obj = TextToSpeech(context) {
                        if (it == TextToSpeech.SUCCESS) cont.resume(TextToSpeechInstanceAndroidModern(obj))
                        else cont.resume(null)
                    }
                } catch (e: SecurityException) {
                    cont.resume(null)
                }
            } else cont.resume(null)
        }
    }
}
