package js_interop

import org.w3c.speech.speechSynthesis

actual typealias Window = org.w3c.dom.Window

actual val window: Window = kotlinx.browser.window

/** @hide */
actual fun getSpeechSynthesis(window: Window) = window.speechSynthesis

/** @hide */
actual val isSpeechSynthesisSupported: Boolean
    get() = js("\"speechSynthesis\" in window") as Boolean
