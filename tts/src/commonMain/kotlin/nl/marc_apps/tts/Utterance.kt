package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalTextToSpeechApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@ExperimentalTextToSpeechApi
class Utterance(val id: Uuid) {
    suspend fun awaitCompletion() {
        TODO()
    }
}
