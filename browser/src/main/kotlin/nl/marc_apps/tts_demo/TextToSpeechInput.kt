package nl.marc_apps.tts_demo

import androidx.compose.runtime.Composable
import nl.marc_apps.tts_demo.strings.Strings
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.TextInput

@Composable
fun TextToSpeechInput(strings: Strings, currentValue: String, valueUpdated: (String) -> Unit) {
    Section {
        Span(attrs = {
            style {
                fontWeight("bold")
            }
        }) {
            Text(strings.ttsTextLabel)
        }

        TextInput(value = currentValue) {
            onInput { event -> valueUpdated(event.value) }
        }
    }
}
