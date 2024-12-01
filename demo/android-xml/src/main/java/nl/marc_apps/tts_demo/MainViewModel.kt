package nl.marc_apps.tts_demo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.TextToSpeechFactory
import nl.marc_apps.tts.TextToSpeechInstance

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var ttsInstance: TextToSpeechInstance? = null

    private val mutableIsTextToSpeechLoaded = MutableStateFlow(false)
    val isTextToSpeechLoaded = mutableIsTextToSpeechLoaded.asStateFlow()

    init {
        viewModelScope.launch {
            initTextToSpeech()
        }
    }

    fun say(text: String?) {
        if (text?.isNotBlank() == true) {
            viewModelScope.launch {
                ttsInstance?.say(text)
            }
        }
    }

    fun setVolume(volume: Float) {
        ttsInstance?.volume = volume.toInt()
    }

    private suspend fun initTextToSpeech() {
        val textToSpeechFactory = TextToSpeechFactory(getApplication<Application>().applicationContext, TextToSpeechEngine.Google)
        ttsInstance = textToSpeechFactory.createOrNull()
        mutableIsTextToSpeechLoaded.update { true }
    }

    override fun onCleared() {
        ttsInstance?.close()
        ttsInstance = null
        super.onCleared()
    }
}