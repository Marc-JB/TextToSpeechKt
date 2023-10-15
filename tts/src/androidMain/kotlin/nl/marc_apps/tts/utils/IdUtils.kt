package nl.marc_apps.tts.utils

import java.util.*

/**
 * @hide
 */
fun getContinuationId(utteranceId: String?): UUID? {
    return if (utteranceId == null) null else try {
        return UUID.fromString(utteranceId)
    } catch (_: Throwable) {
        null
    }
}
