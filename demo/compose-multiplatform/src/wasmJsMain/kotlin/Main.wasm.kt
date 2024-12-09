
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import nl.marc_apps.tts_demo.TtsDemoApp

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("TTS Demo") {
        TtsDemoApp()
    }
}
