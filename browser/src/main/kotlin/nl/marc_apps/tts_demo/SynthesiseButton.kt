package nl.marc_apps.tts_demo

import androidx.compose.runtime.Composable
import nl.marc_apps.tts_demo.strings.Strings
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Text

@Composable
fun SynthesiseButton(strings: Strings, isRunning: Boolean, run: () -> Unit) {
    Section {
        Button(attrs = {
            if (isRunning) {
                disabled()
            }

            onClick {
                run()
            }
        }) {
            Text(strings.actionSay)
        }
    }
}
