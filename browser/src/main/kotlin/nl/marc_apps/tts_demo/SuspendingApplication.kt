package nl.marc_apps.tts_demo

import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import org.w3c.dom.HTMLElement
import kotlin.coroutines.CoroutineContext

abstract class SuspendingApplication : CoroutineScope {
    private var job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    protected inline fun renderUi(crossinline block: TagConsumer<HTMLElement>.() -> Unit) {
        document.body?.append {
            block(this)
        }
    }
}
