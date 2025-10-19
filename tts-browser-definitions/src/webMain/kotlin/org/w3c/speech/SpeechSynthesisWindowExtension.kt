package org.w3c.speech

import org.w3c.dom.Window
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@ExperimentalWasmJsInterop
@Suppress("unused")
external val Window.speechSynthesis: SpeechSynthesis

@ExperimentalWasmJsInterop
@Suppress("unused")
val windowHasSpeechSynthesis: Boolean
    get() = js("\"speechSynthesis\" in window")
