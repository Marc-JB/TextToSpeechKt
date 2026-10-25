package nl.marc_apps.tts.demo

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.window
import nl.marc_apps.tts.demo.resources.Res
import nl.marc_apps.tts.demo.resources.app_name
import org.jetbrains.compose.resources.getString

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        TtsDemoApp()

        LaunchedEffect(Unit) {
            window.document.title = getString(Res.string.app_name)
        }
    }
}
