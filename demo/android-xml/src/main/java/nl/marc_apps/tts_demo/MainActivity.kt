package nl.marc_apps.tts_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechFactory
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts_demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var ttsInstance: TextToSpeechInstance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionSay.isEnabled = false

        lifecycleScope.launch {
            ttsInstance = TextToSpeechFactory(applicationContext).createOrNull()
            binding.actionSay.isEnabled = true

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ttsInstance?.isSynthesizing?.collect {
                    binding.actionSay.isEnabled = !it
                }
            }
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

    override fun onDestroy() {
        ttsInstance?.close()
        super.onDestroy()
    }
}
