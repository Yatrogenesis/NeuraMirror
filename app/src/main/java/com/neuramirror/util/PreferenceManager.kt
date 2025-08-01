package com.neuramirror.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor de preferencias de la aplicación.
 */
@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    
    companion object {
        // Claves de preferencias
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_PROCESSING_MODE = "processing_mode"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_SELECTED_LANGUAGE = "selected_language"
        private const val KEY_VOICE_EMOTION = "voice_emotion"
        private const val KEY_VOICE_SPEED = "voice_speed"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        
        // Valores por defecto
        private const val DEFAULT_PROCESSING_MODE = "cloud"
        private const val DEFAULT_LANGUAGE = "es-MX"
        private const val DEFAULT_VOICE_EMOTION = "neutral"
        private const val DEFAULT_VOICE_SPEED = 1.0f
    }
    
    /**
     * Verifica si el modo oscuro está habilitado.
     */
    fun isDarkModeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }
    
    /**
     * Establece el modo oscuro.
     */
    fun setDarkModeEnabled(enabled: Boolean) {
        prefs.edit {
            putBoolean(KEY_DARK_MODE, enabled)
        }
    }
    
    /**
     * Obtiene el modo de procesamiento (local o cloud).
     */
    fun getProcessingMode(): String {
        return prefs.getString(KEY_PROCESSING_MODE, DEFAULT_PROCESSING_MODE)
            ?: DEFAULT_PROCESSING_MODE
    }
    
    /**
     * Establece el modo de procesamiento.
     */
    fun setProcessingMode(mode: String) {
        prefs.edit {
            putString(KEY_PROCESSING_MODE, mode)
        }
    }
    
    /**
     * Obtiene la clave de API de MiniMax.
     */
    fun getApiKey(): String {
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }
    
    /**
     * Establece la clave de API.
     */
    fun setApiKey(apiKey: String) {
        prefs.edit {
            putString(KEY_API_KEY, apiKey)
        }
    }
    
    /**
     * Obtiene el idioma seleccionado.
     */
    fun getSelectedLanguage(): String {
        return prefs.getString(KEY_SELECTED_LANGUAGE, DEFAULT_LANGUAGE)
            ?: DEFAULT_LANGUAGE
    }
    
    /**
     * Establece el idioma seleccionado.
     */
    fun setSelectedLanguage(language: String) {
        prefs.edit {
            putString(KEY_SELECTED_LANGUAGE, language)
        }
    }
    
    /**
     * Obtiene la emoción de voz predeterminada.
     */
    fun getDefaultVoiceEmotion(): String {
        return prefs.getString(KEY_VOICE_EMOTION, DEFAULT_VOICE_EMOTION)
            ?: DEFAULT_VOICE_EMOTION
    }
    
    /**
     * Establece la emoción de voz predeterminada.
     */
    fun setDefaultVoiceEmotion(emotion: String) {
        prefs.edit {
            putString(KEY_VOICE_EMOTION, emotion)
        }
    }
    
    /**
     * Obtiene la velocidad de voz predeterminada.
     */
    fun getDefaultVoiceSpeed(): Float {
        return prefs.getFloat(KEY_VOICE_SPEED, DEFAULT_VOICE_SPEED)
    }
    
    /**
     * Establece la velocidad de voz predeterminada.
     */
    fun setDefaultVoiceSpeed(speed: Float) {
        prefs.edit {
            putFloat(KEY_VOICE_SPEED, speed)
        }
    }
    
    /**
     * Verifica si es el primer lanzamiento de la aplicación.
     */
    fun isFirstLaunch(): Boolean {
        val isFirst = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        
        // Si es el primer lanzamiento, marcar como falso para próximas veces
        if (isFirst) {
            prefs.edit {
                putBoolean(KEY_FIRST_LAUNCH, false)
            }
        }
        
        return isFirst
    }
    
    /**
     * Restablece a los valores predeterminados.
     */
    fun resetToDefaults() {
        prefs.edit {
            putString(KEY_PROCESSING_MODE, DEFAULT_PROCESSING_MODE)
            putString(KEY_SELECTED_LANGUAGE, DEFAULT_LANGUAGE)
            putString(KEY_VOICE_EMOTION, DEFAULT_VOICE_EMOTION)
            putFloat(KEY_VOICE_SPEED, DEFAULT_VOICE_SPEED)
            // No resetear la clave de API
        }
    }
}
