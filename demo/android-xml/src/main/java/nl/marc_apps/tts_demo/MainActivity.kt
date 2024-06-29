package nl.marc_apps.tts_demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechEngine
import nl.marc_apps.tts.TextToSpeechFactory
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts_demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var ttsInstance: TextToSpeechInstance? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionSay.isEnabled = false
        binding.loadingIndicator.visibility = View.VISIBLE

        lifecycleScope.launch {
            initTextToSpeech()
        }

        binding.actionSay.setOnClickListener {
            val text = binding.inputTtsText.editText?.text?.toString()
            if (text?.isNotBlank() == true) {
                lifecycleScope.launch {
                    ttsInstance?.say(text)
                }
            }
        }

        binding.inputTtsVolume.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                ttsInstance?.volume = value.toInt()
            }
        }

        binding.inputTtsVolume.setLabelFormatter {
            it.toInt().toString()
        }
    }

    private suspend fun initTextToSpeech() {
        val textToSpeechFactory = TextToSpeechFactory(applicationContext, TextToSpeechEngine.Google)

        ttsInstance = textToSpeechFactory.createOrNull()
        binding.loadingIndicator.visibility = View.GONE
        binding.actionSay.isEnabled = true

        lifecycleScope.launch {
            ttsInstance?.currentState?.collect {
                binding.actionSay.isEnabled = it == TextToSpeechInstance.State.QUEUE_EMPTY
                binding.loadingIndicator.visibility = if (it == TextToSpeechInstance.State.LOADING) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        ttsInstance?.close()
        super.onDestroy()
    }
}
