package nl.marc_apps.tts

import nl.marc_apps.tts.experimental.ExperimentalVoiceApi
import java.util.*

@ExperimentalVoiceApi
internal data class DesktopVoice(
    val desktopVoice: com.sun.speech.freetts.Voice,
    override val isDefault: Boolean
): Voice {
    override val name: String = desktopVoice.name
    override val isOnline = false
    override val languageTag: String = desktopVoice.locale.toLanguageTag()
    override val language: String = desktopVoice.locale.displayLanguage
    override val region: String = desktopVoice.locale.displayCountry
    override val locale: Locale = desktopVoice.locale
}
