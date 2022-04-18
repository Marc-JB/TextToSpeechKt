package nl.marc_apps.tts_demo.strings

interface Strings {
    val pageTitle: String

    val ttsTextLabel: String

    val ttsTextDefaultValue: String

    val ttsVolumeLabel: String

    fun ttsVolumeLevel(level: Int): String

    val actionSay: String

    val ttsLanguageLabel: String

    val ttsLanguageUnknown: String

    val ttsLoadingText: String
}
