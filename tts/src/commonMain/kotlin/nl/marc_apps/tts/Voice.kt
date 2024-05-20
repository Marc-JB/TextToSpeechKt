package nl.marc_apps.tts

/**
 * @hide
 */
interface CommonVoice {
    val name: String
    val isDefault: Boolean
    val isOnline: Boolean
    val languageTag: String
    val language: String
    val region: String?
}

expect interface Voice : CommonVoice
