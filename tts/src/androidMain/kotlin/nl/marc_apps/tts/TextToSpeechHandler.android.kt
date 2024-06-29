package nl.marc_apps.tts

import android.annotation.TargetApi
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.annotation.ChecksSdkIntAtLeast
import nl.marc_apps.tts.internal.CallbackQueueHandler
import nl.marc_apps.tts.internal.EnqueueOptions
import nl.marc_apps.tts.internal.TextToSpeechHandler
import nl.marc_apps.tts.utils.TtsProgressConverter
import nl.marc_apps.tts.utils.getContinuationId
import nl.marc_apps.tts.utils.toMap
import java.util.*

@TargetApi(VERSION_CODES.DONUT)
class TextToSpeechHandler(private var tts: TextToSpeech?): TextToSpeechHandler, CallbackQueueHandler {
    override fun createUtteranceId(): Any = UUID.randomUUID()

    override fun enqueue(text: String, utteranceId: Any, options: EnqueueOptions) {
        val queueMode = if(options.clearQueue) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD

        val params = Bundle().apply {
            putFloat(KEY_PARAM_VOLUME, options.volume / 100f)
            putString(KEY_PARAM_UTTERANCE_ID, utteranceId.toString())
        }

        tts?.setPitch(options.pitch)
        tts?.setSpeechRate(options.rate)

        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            tts?.speak(text, queueMode, params, utteranceId.toString())
        } else {
            tts?.speak(text, queueMode, HashMap(params.toMap().mapValues { it.toString() }))
        }
    }

    override fun registerListeners(onStart: (Any) -> Unit, onComplete: (Any, Result<Unit>) -> Unit) {
        if (hasModernProgressListeners) {
            tts?.setOnUtteranceProgressListener(TtsProgressConverter(onStart, onComplete))
        } else {
            tts?.setOnUtteranceCompletedListener {
                val id = getContinuationId(it) ?: return@setOnUtteranceCompletedListener
                onComplete(id, Result.success(Unit))
            }
        }
    }

    override fun clearQueue() {
        tts?.stop()
    }

    override fun close() {
        if (hasModernProgressListeners) {
            tts?.setOnUtteranceProgressListener(null)
        } else {
            tts?.setOnUtteranceCompletedListener(null)
        }
        tts?.shutdown()
        tts = null
    }

    companion object {
        private const val KEY_PARAM_VOLUME = "volume"
        private const val KEY_PARAM_UTTERANCE_ID = "utteranceId"

        @ChecksSdkIntAtLeast(api = VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        private val hasModernProgressListeners = VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1
    }
}
