import androidx.compose.ui.window.Window
import nl.marc_apps.tts_demo.TtsDemoApp
import nl.marc_apps.tts_demo.resources.Res
import nl.marc_apps.tts_demo.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() {
    Window(title = "TTS Demo") {
        window.title = stringResource(Res.string.app_name)
        TtsDemoApp()
    }
}
