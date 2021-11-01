import kotlinx.html.dom.append
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.html.id
import kotlinx.html.js.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.textInput
import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechInstance
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLInputElement
import kotlin.coroutines.CoroutineContext

fun main() {
    val app = Application()
    window.onload = {
        app.onStart()
    }
}

class Application : CoroutineScope {
    private var tts: TextToSpeechInstance? = null

    private var job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    fun onStart() {
        document.body?.append {
            h1 {
                +"Text-to-Speech demo (target: Kotlin/JS)"
            }

            br()

            div {
                label {
                    +"Text: "
                    htmlFor = "tts_text"
                }
                textInput {
                    value = "Hello, world!"
                    id = "tts_text"
                    name = "tts_text"
                }
                br()
                br()
                button {
                    +"Say"
                    id = "tts_button"
                    disabled = true
                    onClickFunction = {
                        val element = document.getElementById("tts_text") as HTMLInputElement
                        tts?.enqueue(element.value, clearQueue = false)
                    }
                }
            }
        }

        launch {
            tts = TextToSpeech.createOrThrow(window)
            val button = document.getElementById("tts_button") as HTMLButtonElement
            button.disabled = false
        }
    }
}
