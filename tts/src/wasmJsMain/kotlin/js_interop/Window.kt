package js_interop

import org.w3c.speech.SpeechSynthesis

actual typealias Window = org.w3c.dom.Window

actual val window: Window = kotlinx.browser.window

/** @hide */
@JsFun("function getBrowserSynthesis() { return window.speechSynthesis; }")
external fun getBrowserSynthesis(): SpeechSynthesis

/** @hide */
actual fun getSpeechSynthesis(window: Window): SpeechSynthesis = getBrowserSynthesis()

/** @hide */
@JsFun("function getIsSpeechSynthesisSupported() { return \"speechSynthesis\" in window; }")
external fun getIsSpeechSynthesisSupported(): Boolean

/** @hide */
actual val isSpeechSynthesisSupported: Boolean
    get() = getIsSpeechSynthesisSupported()
