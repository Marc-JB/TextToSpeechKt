import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.window
import nl.marc_apps.tts_demo.TtsDemoApp
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class, InternalComposeApi::class)
fun main() {
    val darkMode = window.matchMedia("(prefers-color-scheme:dark)").matches
    val systemTheme = if (darkMode) SystemTheme.Dark else SystemTheme.Light

    onWasmReady {
        CanvasBasedWindow("TTS Demo") {
            CompositionLocalProvider(LocalSystemTheme provides systemTheme){
                TtsDemoApp()
            }
        }
    }
}
