package nl.marc_apps.tts

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.ChecksSdkIntAtLeast
import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError
import nl.marc_apps.tts.errors.TextToSpeechSecurityError
import nl.marc_apps.tts.errors.UnknownTextToSpeechInitialisationError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

actual class TextToSpeechFactory(
    private val context: Context
) {
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.DONUT)
    actual val isSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.HONEYCOMB)
    actual val canChangeVolume = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB

    actual suspend fun create(): Result<TextToSpeechInstance> {
        return runCatching { createOrThrow() }
    }

    @Throws(RuntimeException::class)
    actual suspend fun createOrThrow(): TextToSpeechInstance {
        return suspendCoroutine { continuation ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                lateinit var obj: TextToSpeech

                try {
                    obj = TextToSpeech(context) {
                        if (it == TextToSpeech.SUCCESS) {
                            continuation.resume(TextToSpeechAndroid(obj))
                        } else {
                            continuation.resumeWithException(UnknownTextToSpeechInitialisationError())
                        }
                    }
                } catch (e: SecurityException) {
                    continuation.resumeWithException(TextToSpeechSecurityError(e))
                }
            } else continuation.resumeWithException(TextToSpeechNotSupportedError())
        }
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }
}