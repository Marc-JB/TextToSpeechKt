package nl.marc_apps.tts_demo

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.js.*
import kotlinx.html.js.br
import kotlinx.html.js.onClickFunction
import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts_demo.strings.DefaultStrings
import nl.marc_apps.tts_demo.strings.Strings
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
import kotlin.properties.Delegates

class TtsDemoApplication(private val strings: Strings = DefaultStrings) : SuspendingApplication() {
    private var tts: TextToSpeechInstance? = null

    private val inputTtsText by lazy {
        document.getElementById(INPUT_TTS_TEXT_ID) as HTMLInputElement
    }

    private val inputTtsVolume by lazy {
        document.getElementById(INPUT_TTS_VOLUME_ID) as HTMLInputElement
    }

    private val labelTtsVolume by lazy {
        document.getElementById(LABEL_TTS_VOLUME_ID) as HTMLSpanElement
    }

    private val actionSay by lazy {
        document.getElementById(ACTION_SAY_ID) as HTMLButtonElement
    }

    private var isTtsLoading by Delegates.observable(true) { _, _, newValue ->
        actionSay.disabled = newValue
    }

    @ExperimentalJsExport
    fun onStart() {
        renderUi {
            h1 {
                +strings.pageTitle
            }

            br()

            div {
                label {
                    +strings.ttsTextLabel
                    htmlFor = INPUT_TTS_TEXT_ID
                }
                textInput {
                    value = strings.ttsTextDefaultValue
                    id = INPUT_TTS_TEXT_ID
                    name = INPUT_TTS_TEXT_ID
                }
                br()
                br()
                label {
                    +strings.ttsVolumeLabel
                    htmlFor = INPUT_TTS_VOLUME_ID
                }
                rangeInput {
                    value = 100.toString()
                    min = 0.toString()
                    max = 100.toString()
                    id = INPUT_TTS_VOLUME_ID
                    name = INPUT_TTS_VOLUME_ID

                    onInputFunction = {
                        labelTtsVolume.textContent = strings.ttsVolumeLevel(inputTtsVolume.value.toIntOrNull() ?: 100)
                    }
                }
                span {
                    +strings.ttsVolumeLevel(100)
                    id = LABEL_TTS_VOLUME_ID
                }
                br()
                br()
                button {
                    +strings.actionSay
                    id = ACTION_SAY_ID
                    disabled = true
                    onClickFunction = {
                        inputTtsVolume.value.toIntOrNull()?.let {
                            tts?.volume = it
                        }

                        val ttsText = inputTtsText.value

                        if(ttsText.isNotBlank()) {
                            isTtsLoading = true
                            launch {
                                tts?.say(ttsText)
                                isTtsLoading = false
                            }
                        }
                    }
                }
            }
        }

        launch {
            tts = TextToSpeech.createOrThrow(window)
            isTtsLoading = false
        }
    }

    companion object {
        private const val ACTION_SAY_ID = "action_say"

        private const val INPUT_TTS_TEXT_ID = "input_tts_text"

        private const val INPUT_TTS_VOLUME_ID = "input_tts_volume"

        private const val LABEL_TTS_VOLUME_ID = "label_tts_volume"
    }
}
