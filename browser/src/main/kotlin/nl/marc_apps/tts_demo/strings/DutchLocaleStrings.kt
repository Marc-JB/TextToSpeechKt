package nl.marc_apps.tts_demo.strings

object DutchLocaleStrings : Strings {
    override val pageTitle = "Tekst-naar-Spraak voorbeeld (doel: Kotlin/JS)"

    override val ttsTextLabel = "Tekst: "

    override val ttsTextDefaultValue = "Hallo, wereld!"

    override val ttsVolumeLabel = "Volume: "

    override fun ttsVolumeLevel(level: Int) = " ($level%)"

    override val actionSay = "Zeg"
}
