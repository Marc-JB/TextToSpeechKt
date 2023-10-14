package org.w3c.speech

import org.w3c.dom.Window

inline val Window.speechSynthesis: SpeechSynthesis
    get() = asDynamic().speechSynthesis as SpeechSynthesis
