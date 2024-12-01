package nl.marc_apps.tts_demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechFactory
import nl.marc_apps.tts.TextToSpeechInstance
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MainViewModel(private val textToSpeechFactory: TextToSpeechFactory) : ViewModel() {
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
        ttsInstance = textToSpeechFactory.createOrNull()
        mutableIsTextToSpeechLoaded.update { true }
    }

    override fun onCleared() {
        ttsInstance?.close()
        ttsInstance = null
        super.onCleared()
    }
}