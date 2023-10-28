package org.w3c.speech

actual abstract external class Window

external val window: Window

actual fun getWindow() = window

@JsFun("function getBrowserSynthesis() { return window.speechSynthesis; }")
external fun getBrowserSynthesis(): SpeechSynthesis

actual fun getSynthesis(window: Window): SpeechSynthesisCommon = SpeechSynthesisImpl()
