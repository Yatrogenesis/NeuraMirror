package com.neuramirror.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.neuramirror.util.Constants
import com.neuramirror.util.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val preferenceManager: PreferenceManager
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    
    // Tema
    fun isDarkModeEnabled(): Boolean {
        return preferenceManager.isDarkModeEnabled()
    }
    
    fun setDarkModeEnabled(enabled: Boolean) {
        preferenceManager.setDarkModeEnabled(enabled)
    }
    
    // Modo de procesamiento
    fun getProcessingMode(): String {
        return preferenceManager.getProcessingMode()
    }
    
    fun setProcessingMode(mode: String) {
        preferenceManager.setProcessingMode(mode)
    }
    
    // Clave API
    fun getApiKey(): String {
        return preferenceManager.getApiKey()
    }
    
    fun setApiKey(apiKey: String) {
        preferenceManager.setApiKey(apiKey)
    }
    
    // Idioma
    fun getSelectedLanguage(): String {
        return preferenceManager.getSelectedLanguage()
    }
    
    fun setSelectedLanguage(language: String) {
        preferenceManager.setSelectedLanguage(language)
    }
    
    // Limpiar caché
    fun clearCache() {
        val cacheDir = context.cacheDir
        val generatedDir = File(context.filesDir, Constants.GENERATED_DIR)
        
        try {
            deleteDir(cacheDir)
            deleteDir(generatedDir)
            Timber.d("Caché limpiada correctamente")
        } catch (e: Exception) {
            Timber.e(e, "Error al limpiar caché")
        }
    }
    
    // Restablecer configuración
    fun resetToDefaults() {
        preferenceManager.resetToDefaults()
        Timber.d("Configuración restablecida correctamente")
    }
    
    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children = dir.list()
            if (children != null) {
                for (child in children) {
                    val success = deleteDir(File(dir, child))
                    if (!success) {
                        return false
                    }
                }
            }
        }
        
        return dir.delete()
    }
}
