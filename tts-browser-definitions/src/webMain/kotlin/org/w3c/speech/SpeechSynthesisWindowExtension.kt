package org.w3c.speech

import org.w3c.dom.Window
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@ExperimentalWasmJsInterop
@Suppress("unused")
private fun getSpeechSynthesis(context: Window): SpeechSynthesis = js("context.speechSynthesis")

@ExperimentalWasmJsInterop
@Suppress("unused")
private fun hasSpeechSynthesis(context: Window): Boolean = js("\"speechSynthesis\" in context")

@ExperimentalWasmJsInterop
@Suppress("unused")
val Window.speechSynthesis: SpeechSynthesis?
    get() = if (hasSpeechSynthesis(this)) getSpeechSynthesis(this) else null
