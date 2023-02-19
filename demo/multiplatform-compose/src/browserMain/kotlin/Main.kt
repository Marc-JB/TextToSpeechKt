import kotlinx.browser.window
import nl.marc_apps.tts_demo.TtsDemoApp
import org.jetbrains.skiko.wasm.onWasmReady

fun main() {
    val darkMode = window.matchMedia("(prefers-color-scheme:dark)").matches

    onWasmReady {
        BrowserViewportWindow("TTS Demo") {
            TtsDemoApp(darkMode, false)
        }
    }
}
