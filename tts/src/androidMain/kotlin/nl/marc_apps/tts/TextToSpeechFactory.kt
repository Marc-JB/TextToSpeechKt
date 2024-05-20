package nl.marc_apps.tts

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import androidx.annotation.ChecksSdkIntAtLeast
import nl.marc_apps.tts.errors.TextToSpeechNotSupportedError
import nl.marc_apps.tts.errors.UnknownTextToSpeechInitialisationError
import nl.marc_apps.tts.utils.ErrorCodes
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Factory class to create a Text-to-Speech instance.
 * Use [defaultEngine] to set the package name of the default engine.
 * Setting this to [TextToSpeechEngine.Google] is recommended.
 */
actual class TextToSpeechFactory(
    private val context: Context,
    private val defaultEngine: TextToSpeechEngine = TextToSpeechEngine.SystemDefault
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
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.DONUT) {
                continuation.resumeWithException(TextToSpeechNotSupportedError())
                return@suspendCoroutine
            }

            lateinit var obj: TextToSpeech

            try {
                val initListener = TextToSpeech.OnInitListener { responseCode ->
                    if (responseCode == TextToSpeech.SUCCESS) {
                        continuation.resume(TextToSpeechAndroid(obj))
                    } else {
                        continuation.resumeWithException(ErrorCodes.mapToThrowable(responseCode))
                    }
                }

                obj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    TextToSpeech(context, initListener, defaultEngine.androidPackage)
                } else {
                    TextToSpeech(context, initListener)
                }
            } catch (e: Throwable) {
                continuation.resumeWithException(UnknownTextToSpeechInitialisationError(e))
            }
        }
    }

    actual suspend fun createOrNull(): TextToSpeechInstance? {
        return create().getOrNull()
    }

    companion object {
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.DONUT)
        val isSupported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.HONEYCOMB)
        val canChangeVolume = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
    }
}
