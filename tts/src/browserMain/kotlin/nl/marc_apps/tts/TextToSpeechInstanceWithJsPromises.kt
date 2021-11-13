package nl.marc_apps.tts

import kotlin.js.Promise

interface TextToSpeechInstanceWithJsPromises : TextToSpeechInstance {
    fun sayJsPromise(
        text: String,
        clearQueue: Boolean = false,
        resumeOnStatus: TextToSpeechInstance.Status = TextToSpeechInstance.Status.FINISHED
    ): Promise<Unit>
}
