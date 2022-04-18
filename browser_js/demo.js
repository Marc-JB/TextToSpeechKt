'use strict';
/**
 * @type {import('./tts/v1/tts').tts.nl.marc_apps.tts.TextToSpeechInstanceJS | null}
 */
let ttsInstance = null

/**
 * @type {HTMLInputElement | null}
 */
let inputTtsText = null

/**
 * @type {HTMLInputElement | null}
 */
let inputTtsVolume = null

/**
 * @type {HTMLSpanElement | null}
 */
let labelTtsVolume = null

/**
 * @type {HTMLSpanElement | null}
 */
let labelTtsLanguage = null

function main() {
    ttsInstance = tts.createTtsOrThrow()
    inputTtsText = document.getElementById("input_tts_text")
    inputTtsVolume = document.getElementById("input_tts_volume")
    labelTtsVolume = document.getElementById("label_tts_volume")
    labelTtsLanguage = document.getElementById("label_tts_language")
    
    setTimeout(() => {
        labelTtsLanguage.textContent = ttsInstance.language
    }, 500)
}

function onNewVolumeInput() {
    if (labelTtsVolume !== null && inputTtsVolume !== null) {
        labelTtsVolume.textContent = ` (${inputTtsVolume.value}%)`
    }
}

function isBlank(str) {
    return (!str || /^\s*$/.test(str));
}

function onSayClicked() {
    const volumeInput = parseInt(inputTtsVolume.value)
    if (!isNaN(volumeInput) && ttsInstance !== null) {
        ttsInstance.volume = volumeInput
    }

    const ttsText = inputTtsText.value
    if (!isBlank(ttsText)) {
        ttsInstance.enqueue(ttsText)
    }
}
