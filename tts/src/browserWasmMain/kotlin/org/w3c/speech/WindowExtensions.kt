package org.w3c.speech

import js_interop.VoiceIterable

actual abstract external class Window

external val window: Window

actual fun getWindow() = window

@JsFun("function getBrowserSynthesis() { return window.speechSynthesis; }")
external fun getBrowserSynthesis(): js_interop.SpeechSynthesis

@JsFun("function getVoices() { return window.speechSynthesis.getVoices()[Symbol.iterator]() }")
external fun getBrowserSynthesisVoices(): VoiceIterable

actual fun getSynthesis(window: Window): SpeechSynthesis = SpeechSynthesis()
