import androidx.compose.ui.window.singleWindowApplication
import nl.marc_apps.tts_demo.TtsDemoApp
import nl.marc_apps.tts_demo.resources.Res
import nl.marc_apps.tts_demo.resources.app_name
import org.jetbrains.compose.resources.getString

suspend fun main() {
    singleWindowApplication(
        title = getString(Res.string.app_name)
    ) {
        TtsDemoApp()
    }
}
