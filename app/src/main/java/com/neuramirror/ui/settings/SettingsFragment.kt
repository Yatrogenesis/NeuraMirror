package com.neuramirror.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.neuramirror.R
import com.neuramirror.databinding.FragmentSettingsBinding
import com.neuramirror.ui.viewmodel.SettingsViewModel
import com.neuramirror.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SettingsViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        loadSettings()
        setupListeners()
    }
    
    private fun setupUI() {
        // Configurar spinner de idiomas
        val languages = Constants.SUPPORTED_LANGUAGES.values.toList()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            languages
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter
    }
    
    private fun loadSettings() {
        // Cargar tema
        val isDarkMode = viewModel.isDarkModeEnabled()
        when {
            isDarkMode -> binding.rbThemeDark.isChecked = true
            else -> binding.rbThemeLight.isChecked = true
        }
        
        // Cargar modo de procesamiento
        val isCloudMode = viewModel.getProcessingMode() == Constants.PROCESSING_MODE_CLOUD
        if (isCloudMode) {
            binding.rbProcessingCloud.isChecked = true
        } else {
            binding.rbProcessingLocal.isChecked = true
        }
        
        // Cargar clave API
        binding.etApiKey.setText(viewModel.getApiKey())
        
        // Cargar idioma seleccionado
        val selectedLanguage = viewModel.getSelectedLanguage()
        val languageIndex = Constants.SUPPORTED_LANGUAGES.keys.indexOf(selectedLanguage)
        if (languageIndex >= 0) {
            binding.spinnerLanguage.setSelection(languageIndex)
        }
    }
    
    private fun setupListeners() {
        // Botón para guardar configuración
        binding.btnSaveSettings.setOnClickListener {
            saveSettings()
        }
        
        // Botón para limpiar cache
        binding.btnClearCache.setOnClickListener {
            showClearCacheDialog()
        }
        
        // Botón para restablecer configuración
        binding.btnResetSettings.setOnClickListener {
            showResetSettingsDialog()
        }
    }
    
    private fun saveSettings() {
        // Guardar tema
        val darkModeEnabled = binding.rbThemeDark.isChecked
        viewModel.setDarkModeEnabled(darkModeEnabled)
        
        // Guardar modo de procesamiento
        val processingMode = if (binding.rbProcessingCloud.isChecked) {
            Constants.PROCESSING_MODE_CLOUD
        } else {
            Constants.PROCESSING_MODE_LOCAL
        }
        viewModel.setProcessingMode(processingMode)
        
        // Guardar clave API
        val apiKey = binding.etApiKey.text.toString()
        viewModel.setApiKey(apiKey)
        
        // Guardar idioma seleccionado
        val languageIndex = binding.spinnerLanguage.selectedItemPosition
        if (languageIndex >= 0) {
            val languageCode = Constants.SUPPORTED_LANGUAGES.keys.toList()[languageIndex]
            viewModel.setSelectedLanguage(languageCode)
        }
        
        // Mostrar mensaje de éxito
        Snackbar.make(
            binding.root,
            R.string.save_settings,
            Snackbar.LENGTH_SHORT
        ).show()
        
        Timber.d("Configuración guardada")
    }
    
    private fun showClearCacheDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_clear_cache_title)
            .setMessage(R.string.dialog_clear_cache_message)
            .setPositiveButton(R.string.action_confirm) { _, _ ->
                viewModel.clearCache()
                Snackbar.make(
                    binding.root,
                    R.string.clear_cache,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }
    
    private fun showResetSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_reset_settings_title)
            .setMessage(R.string.dialog_reset_settings_message)
            .setPositiveButton(R.string.action_confirm) { _, _ ->
                viewModel.resetToDefaults()
                loadSettings() // Recargar ajustes
                Snackbar.make(
                    binding.root,
                    R.string.reset_settings,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
