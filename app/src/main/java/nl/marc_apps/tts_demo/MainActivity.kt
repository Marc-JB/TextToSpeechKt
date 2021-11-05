package nl.marc_apps.tts_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeech
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts_demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var ttsInstance: TextToSpeechInstance? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionSay.isEnabled = false

        lifecycleScope.launch {
            ttsInstance = TextToSpeech.createOrNull(applicationContext)
            binding.actionSay.isEnabled = true
        }

        binding.actionSay.setOnClickListener {
            val text = binding.inputTtsText.editText?.text?.toString()
            if (!text.isNullOrEmpty()) {
                lifecycleScope.launch {
                    binding.actionSay.isEnabled = false
                    ttsInstance?.say(text, clearQueue = false, resumeOnStatus = TextToSpeechInstance.Status.FINISHED)
                    binding.actionSay.isEnabled = true
                }
            }
        }
    }

    override fun onDestroy() {
        ttsInstance?.close()
        super.onDestroy()
    }
}
