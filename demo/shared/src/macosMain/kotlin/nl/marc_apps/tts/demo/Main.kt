package nl.marc_apps.tts.demo

import androidx.compose.ui.window.Window
import nl.marc_apps.tts.demo.resources.Res
import nl.marc_apps.tts.demo.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() {
    Window(title = "TTS Demo") {
        window.title = stringResource(Res.string.app_name)
        TtsDemoApp()
    }
}
