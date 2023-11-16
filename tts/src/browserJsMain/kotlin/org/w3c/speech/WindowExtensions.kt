package org.w3c.speech

import kotlinx.browser.window

actual typealias Window = org.w3c.dom.Window

actual fun getWindow() = window

actual fun getSynthesis(window: Window) = window.speechSynthesis

inline val Window.speechSynthesis: SpeechSynthesis
    get() = asDynamic().speechSynthesis as SpeechSynthesis
