import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.window
import nl.marc_apps.tts_demo.TtsDemoApp
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val darkMode = window.matchMedia("(prefers-color-scheme:dark)").matches

    onWasmReady {
        CanvasBasedWindow("TTS Demo") {
            TtsDemoApp(darkMode, false)
        }
    }
}
