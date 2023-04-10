package nl.marc_apps.tts_demo

import androidx.compose.runtime.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts_demo.strings.DefaultStrings
import nl.marc_apps.tts_demo.strings.DutchLocaleStrings
import nl.marc_apps.tts_demo.strings.Strings
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Section
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

fun getStrings(): Strings {
    val language = window.navigator.language
    return when {
        language == "nl" || language.startsWith("nl") -> DutchLocaleStrings
        else -> DefaultStrings
    }
}

suspend fun createTts(): TextToSpeechInstance {
    val tts = TextToSpeech.createOrThrow(window)
    delay(500)
    return tts
}

@Composable
fun AppLayout() {
    Style(AppStyle)

    val strings by lazy { getStrings() }

    val coroutineScope = rememberCoroutineScope()

    var ttsState by remember { mutableStateOf<TextToSpeechInstance?>(null) }

    var isSynthesisRunning by remember { mutableStateOf(false) }

    var ttsTextState by remember { mutableStateOf(strings.ttsTextDefaultValue) }

    H1 {
        Text(strings.pageTitle)
    }

    if (ttsState == null) {
        Span {
            Text(strings.ttsLoadingText)
        }
    } else {
        var ttsVolumeState by remember { mutableStateOf(ttsState?.volume ?: TextToSpeechInstance.VOLUME_DEFAULT) }

        TextToSpeechInput(strings, ttsTextState) {
            ttsTextState = it
        }

        VolumeControls(strings, ttsVolumeState) {
            ttsVolumeState = it
        }

        Section {
            Span(attrs = {
                style {
                    fontWeight("bold")
                }
            }) {
                Text(strings.ttsLanguageLabel)
            }

            Span {
                Text(ttsState?.language ?: strings.ttsLanguageUnknown)
            }
        }

        SynthesiseButton(strings, isSynthesisRunning) {
            isSynthesisRunning = true
            ttsState?.volume = ttsVolumeState
            coroutineScope.launch {
                ttsState?.say(ttsTextState)
                isSynthesisRunning = false
            }
        }
    }

    coroutineScope.launch {
        ttsState = createTts()
    }
}
