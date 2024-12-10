package nl.marc_apps.tts_demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import nl.marc_apps.tts_demo.databinding.FragmentMainXmlBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainXmlFragment : Fragment() {
    private lateinit var binding: FragmentMainXmlBinding

    private val viewModel by viewModel<MainXmlViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainXmlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionSay.isEnabled = false
        binding.loadingIndicator.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isTextToSpeechLoaded.collect { hasLoaded ->
                    binding.actionSay.isEnabled = hasLoaded
                    binding.loadingIndicator.visibility = if (hasLoaded) View.GONE else View.VISIBLE
                }
            }
        }

        binding.actionSay.setOnClickListener {
            val text = binding.inputTtsText.editText?.text?.toString()
            viewModel.say(text)
        }

        binding.inputTtsVolume.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.setVolume(value)
            }
        }

        binding.inputTtsVolume.setLabelFormatter {
            it.toInt().toString()
        }
    }
}