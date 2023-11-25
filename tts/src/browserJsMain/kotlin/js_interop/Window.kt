package js_interop

import org.w3c.speech.speechSynthesis

actual typealias Window = org.w3c.dom.Window

actual val window: Window = kotlinx.browser.window

actual fun getSpeechSynthesis(window: Window) = window.speechSynthesis
