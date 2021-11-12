package nl.marc_apps.tts_demo.strings

object DefaultStrings : Strings {
    override val pageTitle = "Text-to-Speech demo (target: Kotlin/JS)"

    override val ttsTextLabel = "Text: "

    override val ttsTextDefaultValue = "Hello, world!"

    override val ttsVolumeLabel = "Volume: "

    override fun ttsVolumeLevel(level: Int) = " ($level%)"

    override val actionSay = "Say"
}
