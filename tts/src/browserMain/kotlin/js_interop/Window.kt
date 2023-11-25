package js_interop

import org.w3c.speech.SpeechSynthesis

expect abstract class Window

expect val window: Window

expect fun getSpeechSynthesis(window: Window): SpeechSynthesis
