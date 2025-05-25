
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.window
import nl.marc_apps.tts_demo.TtsDemoApp
import nl.marc_apps.tts_demo.resources.Res
import nl.marc_apps.tts_demo.resources.app_name
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow {
        TtsDemoApp()

        LaunchedEffect(Unit) {
            window.document.title = getString(Res.string.app_name)
        }
    }
}
