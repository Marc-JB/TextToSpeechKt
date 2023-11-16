package org.w3c.speech

expect abstract class Window

expect fun getWindow(): Window

expect fun getSynthesis(window: Window): SpeechSynthesis
