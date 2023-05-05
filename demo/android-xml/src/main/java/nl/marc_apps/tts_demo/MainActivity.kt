package nl.marc_apps.tts_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
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
        val textToSpeechFactory = TextToSpeechFactory(applicationContext, TextToSpeechFactory.ENGINE_SPEECH_SERVICES_BY_GOOGLE)

        ttsInstance = textToSpeechFactory.createOrNull()
        binding.loadingIndicator.visibility = View.GONE
        binding.actionSay.isEnabled = true

        lifecycleScope.launch {
            ttsInstance?.isSynthesizing?.collect {
                binding.actionSay.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            ttsInstance?.isWarmingUp?.collect {
                if (it) {
                    binding.actionSay.isEnabled = false
                }
                binding.loadingIndicator.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroy() {
        ttsInstance?.close()
        super.onDestroy()
    }
}
