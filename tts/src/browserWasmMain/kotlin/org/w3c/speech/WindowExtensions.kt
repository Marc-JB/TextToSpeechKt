package org.w3c.speech

import org.w3c.dom.Window

@JsFun("function getBrowserSynthesis() { return window.speechSynthesis; }")
external fun getBrowserSynthesis(): SpeechSynthesis

fun getSynthesis(window: Window): SpeechSynthesis = SpeechSynthesis()