package nl.marc_apps.tts.demo

import androidx.compose.ui.window.singleWindowApplication
import nl.marc_apps.tts.demo.resources.Res
import nl.marc_apps.tts.demo.resources.app_name
import org.jetbrains.compose.resources.getString

suspend fun main() {
    singleWindowApplication(
        title = getString(Res.string.app_name)
    ) {
        TtsDemoApp()
    }
}
