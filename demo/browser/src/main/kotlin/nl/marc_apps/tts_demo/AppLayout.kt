package nl.marc_apps.tts_demo

import androidx.compose.runtime.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechFactory
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

@Composable
fun rememberTextToSpeechOrNull(): TextToSpeechInstance? {
    var textToSpeech by remember { mutableStateOf<TextToSpeechInstance?>(null) }

    LaunchedEffect(Unit) {
        val tts = TextToSpeechFactory(window).createOrNull()
        delay(500)
        textToSpeech = tts
    }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.close()
        }
    }

    return textToSpeech
}

@Composable
fun AppLayout() {
    Style(AppStyle)

    val strings by lazy { getStrings() }

    val coroutineScope = rememberCoroutineScope()

    val textToSpeechInstance = rememberTextToSpeechOrNull()

    var ttsTextState by remember { mutableStateOf(strings.ttsTextDefaultValue) }

    H1 {
        Text(strings.pageTitle)
    }

    if (textToSpeechInstance == null) {
        Span {
            Text(strings.ttsLoadingText)
        }
    } else {
        val isSynthesisRunning by textToSpeechInstance.isSynthesizing.collectAsState()

        var ttsVolumeState by remember { mutableStateOf(textToSpeechInstance.volume) }

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
                Text(textToSpeechInstance.language)
            }
        }

        SynthesiseButton(strings, isSynthesisRunning) {
            textToSpeechInstance.volume = ttsVolumeState
            coroutineScope.launch {
                textToSpeechInstance.say(ttsTextState)
            }
        }
    }
}
