import kotlinx.browser.window
import nl.marc_apps.tts_demo.TtsDemoApplication
import nl.marc_apps.tts_demo.strings.DefaultStrings
import nl.marc_apps.tts_demo.strings.DutchLocaleStrings

@ExperimentalJsExport
fun main() {
    val language = window.navigator.language
    val strings = when {
        language == "nl" || language.startsWith("nl") -> DutchLocaleStrings
        else -> DefaultStrings
    }

    val app = TtsDemoApplication(strings)

    window.onload = {
        app.onStart()
    }
}
