package js_interop

import org.w3c.speech.SpeechSynthesis

actual typealias Window = org.w3c.dom.Window

actual val window: Window = kotlinx.browser.window

@JsFun("function getBrowserSynthesis() { return window.speechSynthesis; }")
external fun getBrowserSynthesis(): SpeechSynthesis

fun getSpeechSynthesis(window: Window): SpeechSynthesis = getBrowserSynthesis()
