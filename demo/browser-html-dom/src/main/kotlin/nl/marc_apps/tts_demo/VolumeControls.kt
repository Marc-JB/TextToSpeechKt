package nl.marc_apps.tts_demo

import androidx.compose.runtime.Composable
import nl.marc_apps.tts_demo.strings.Strings
import org.jetbrains.compose.web.css.fontWeight
import org.jetbrains.compose.web.dom.RangeInput
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun VolumeControls(strings: Strings, volume: Int, onVolumeChanged: (Int) -> Unit) {
    Section {
        Span(attrs = {
            style {
                fontWeight("bold")
            }
        }) {
            Text(strings.ttsVolumeLabel)
        }

        RangeInput(value = volume, min = 0, max = 100) {
            onInput { event -> event.value?.toInt()?.let { onVolumeChanged(it) } }
        }

        Span {
            Text(strings.ttsVolumeLevel(volume))
        }
    }
}
