package com.neuramirror.ui.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.neuramirror.BuildConfig
import com.neuramirror.databinding.FragmentHelpBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
    }
    
    private fun setupUI() {
        // Mostrar versión de la aplicación
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        binding.tvVersionInfo.text = "Versión $versionName ($versionCode)"
        
        // Configurar botones
        setupButtons()
    }
    
    private fun setupButtons() {
        // Botón de correo electrónico
        binding.btnEmail.setOnClickListener {
            sendEmail()
        }
        
        // Botón de sitio web
        binding.btnWebsite.setOnClickListener {
            openWebsite("https://neuramirror.ai")
        }
        
        // Botón de términos y condiciones
        binding.btnTerms.setOnClickListener {
            openWebsite("https://neuramirror.ai/terms")
        }
        
        // Botón de política de privacidad
        binding.btnPrivacyPolicy.setOnClickListener {
            openWebsite("https://neuramirror.ai/privacy")
        }
        
        // Botón de licencias de terceros
        binding.btnThirdPartyLicenses.setOnClickListener {
            showThirdPartyLicenses()
        }
    }
    
    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@neuramirror.ai")
            putExtra(Intent.EXTRA_SUBJECT, "Soporte NeuraMirror")
        }
        
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Timber.e("No se encontró aplicación para enviar correo")
        }
    }
    
    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        } else {
            Timber.e("No se encontró navegador web")
        }
    }
    
    private fun showThirdPartyLicenses() {
        // Implementar pantalla de licencias de terceros
        // Puedes usar OssLicensesMenuActivity de Google Play Services
        Timber.d("Mostrar licencias de terceros")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
