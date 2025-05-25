package nl.marc_apps.tts.utils

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * @hide
 */
@OptIn(ExperimentalUuidApi::class)
fun getContinuationId(utteranceId: String?): Uuid? {
    return if (utteranceId == null) null else try {
        return Uuid.parse(utteranceId)
    } catch (_: Throwable) {
        null
    }
}
