package com.neuramirror.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.neuramirror.R
import com.neuramirror.databinding.FragmentHomeBinding
import com.neuramirror.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainViewModel by activityViewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Configurar controles de grabación
        binding.btnRecord.setOnClickListener {
            viewModel.startRecording()
        }
        
        binding.btnSelectFile.setOnClickListener {
            // Implementar selección de archivo
            Timber.d("Seleccionar archivo de audio")
        }
        
        binding.btnProcessRecording.setOnClickListener {
            viewModel.processRecording()
        }
        
        // Configurar controles de síntesis
        binding.btnGenerateVoice.setOnClickListener {
            val text = binding.etTextToSynthesize.text.toString()
            viewModel.synthesizeVoice(text)
        }
        
        binding.btnPlayGenerated.setOnClickListener {
            viewModel.playGeneratedAudio()
        }
        
        // Configurar selector de emociones
        binding.chipGroupEmotions.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val emotion = when (checkedIds[0]) {
                    R.id.chipNeutral -> "neutral"
                    R.id.chipHappy -> "happy"
                    R.id.chipSad -> "sad"
                    R.id.chipAngry -> "angry"
                    R.id.chipSurprised -> "surprised"
                    else -> "neutral"
                }
                
                val emotionParams = mapOf(emotion to 1.0f)
                viewModel.updateEmotionParams(emotionParams)
            }
        }
    }
    
    private fun observeViewModel() {
        // Observar estado de grabación
        viewModel.isRecording.observe(viewLifecycleOwner) { isRecording ->
            binding.btnRecord.isEnabled = !isRecording
            binding.btnSelectFile.isEnabled = !isRecording
            binding.btnProcessRecording.isEnabled = !isRecording && viewModel.currentRecordingFile != null
        }
        
        // Observar estado de procesamiento
        viewModel.isProcessing.observe(viewLifecycleOwner) { isProcessing ->
            binding.btnProcessRecording.isEnabled = !isProcessing && viewModel.currentRecordingFile != null
            binding.btnGenerateVoice.isEnabled = !isProcessing && viewModel.currentVoiceModel.value != null
            binding.btnPlayGenerated.isEnabled = !isProcessing && viewModel.generatedAudioFile.value != null
        }
        
        // Observar modelo de voz actual
        viewModel.currentVoiceModel.observe(viewLifecycleOwner) { model ->
            val hasModel = model != null
            binding.etTextToSynthesize.isEnabled = hasModel
            binding.chipNeutral.isEnabled = hasModel
            binding.chipHappy.isEnabled = hasModel
            binding.chipSad.isEnabled = hasModel
            binding.chipAngry.isEnabled = hasModel
            binding.chipSurprised.isEnabled = hasModel
            binding.btnGenerateVoice.isEnabled = hasModel
        }
        
        // Observar texto a sintetizar
        viewModel.textToSynthesize.observe(viewLifecycleOwner) { text ->
            if (binding.etTextToSynthesize.text.toString() != text) {
                binding.etTextToSynthesize.setText(text)
            }
        }
        
        // Observar archivo de audio generado
        viewModel.generatedAudioFile.observe(viewLifecycleOwner) { file ->
            binding.btnPlayGenerated.isEnabled = file != null
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
