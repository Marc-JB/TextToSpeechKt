package js_interop

import org.w3c.speech.SpeechSynthesis

expect abstract class Window

expect val window: Window

/** @hide */
expect fun getSpeechSynthesis(window: Window): SpeechSynthesis

/** @hide */
expect val isSpeechSynthesisSupported: Boolean
