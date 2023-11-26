import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.window.CanvasBasedWindow
import kotlinx.browser.window
import nl.marc_apps.tts_demo.TtsDemoApp

@OptIn(ExperimentalComposeUiApi::class, InternalComposeApi::class)
fun main() {
    val darkMode = window.matchMedia("(prefers-color-scheme:dark)").matches
    val systemTheme = if (darkMode) SystemTheme.Dark else SystemTheme.Light

    CanvasBasedWindow("TTS Demo") {
        CompositionLocalProvider(LocalSystemTheme provides systemTheme){
            TtsDemoApp()
        }
    }
}
