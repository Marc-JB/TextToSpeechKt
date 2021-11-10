import kotlinx.browser.window
import nl.marc_apps.tts_demo.TtsDemoApplication

@ExperimentalJsExport
fun main() {
    val app = TtsDemoApplication()
    window.onload = {
        app.onStart()
    }
}
