package com.neuramirror

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.neuramirror.util.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class NeuraMirrorApp : Application() {

    @Inject
    lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Timber para el registro de logs
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Aplicar tema según preferencias
        val isDarkMode = preferenceManager.isDarkModeEnabled()
        val nightMode = if (isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
        
        // Crear directorios necesarios
        createRequiredDirectories()
        
        Timber.d("NeuraMirror App inicializada")
    }
    
    private fun createRequiredDirectories() {
        val directories = listOf(
            "audio",
            "audio/recordings",
            "audio/generated",
            "aion_models"
        )
        
        directories.forEach { dir ->
            val directory = filesDir.resolve(dir)
            if (!directory.exists()) {
                val created = directory.mkdirs()
                if (created) {
                    Timber.d("Directorio creado: $dir")
                } else {
                    Timber.e("Error al crear directorio: $dir")
                }
            }
        }
    }
}
