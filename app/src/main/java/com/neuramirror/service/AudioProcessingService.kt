package com.neuramirror.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.neuramirror.R
import com.neuramirror.aion.AionProtocol
import com.neuramirror.aion.processor.AudioProcessor
import com.neuramirror.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Servicio para procesamiento de audio en segundo plano.
 */
@AndroidEntryPoint
class AudioProcessingService : Service() {

    @Inject
    lateinit var audioProcessor: AudioProcessor
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val binder = LocalBinder()
    private val aionProtocol = AionProtocol.getInstance()
    
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "NeuraMirror_Channel"
    
    // Estados
    private var isProcessing = false
    private var currentProcessingFile: File? = null
    
    inner class LocalBinder : Binder() {
        fun getService(): AudioProcessingService = this@AudioProcessingService
    }
    
    override fun onCreate() {
        super.onCreate()
        Timber.d("AudioProcessingService creado")
        
        // Crear canal de notificación para Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("AudioProcessingService iniciado")
        
        // Iniciar como servicio en primer plano con notificación
        startForeground(NOTIFICATION_ID, createNotification())
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Timber.d("AudioProcessingService destruido")
        
        // Cancelar tareas en curso
        serviceScope.cancel()
    }
    
    /**
     * Procesa un archivo de audio para generar un modelo de voz.
     */
    fun processAudioFile(audioFile: File, callback: (String?, Exception?) -> Unit) {
        if (isProcessing) {
            callback(null, IllegalStateException("Ya hay un procesamiento en curso"))
            return
        }
        
        isProcessing = true
        currentProcessingFile = audioFile
        updateNotification(getString(R.string.processing_voice))
        
        serviceScope.launch {
            try {
                // Extraer características de audio
                val audioFeatures = audioProcessor.extractFeatures(audioFile)
                
                // Generar modelo de voz
                val modelId = aionProtocol.processVoiceSample(audioFile)
                
                // Actualizar notificación
                updateNotification(getString(R.string.voice_model_created))
                
                // Notificar éxito
                callback(modelId, null)
                
            } catch (e: Exception) {
                Timber.e(e, "Error al procesar audio: ${e.message}")
                callback(null, e)
            } finally {
                isProcessing = false
                currentProcessingFile = null
            }
        }
    }
    
    /**
     * Sintetiza voz a partir de texto y un modelo.
     */
    fun synthesizeVoice(
        text: String,
        voiceModelId: String,
        outputFile: File,
        emotionParams: Map<String, Float>? = null,
        callback: (Boolean, Exception?) -> Unit
    ) {
        if (isProcessing) {
            callback(false, IllegalStateException("Ya hay un procesamiento en curso"))
            return
        }
        
        isProcessing = true
        updateNotification(getString(R.string.synthesizing_voice))
        
        serviceScope.launch {
            try {
                // Sintetizar voz
                val success = aionProtocol.synthesizeVoice(
                    text = text,
                    voiceModelId = voiceModelId,
                    outputFile = outputFile,
                    emotionParams = emotionParams
                )
                
                // Actualizar notificación
                updateNotification(
                    if (success) getString(R.string.voice_generated)
                    else getString(R.string.error_synthesizing)
                )
                
                // Notificar resultado
                callback(success, null)
                
            } catch (e: Exception) {
                Timber.e(e, "Error al sintetizar voz: ${e.message}")
                callback(false, e)
            } finally {
                isProcessing = false
            }
        }
    }
    
    /**
     * Crea una notificación para el servicio en primer plano.
     */
    private fun createNotification(): Notification {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.service_running))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    /**
     * Actualiza la notificación con un nuevo mensaje.
     */
    private fun updateNotification(message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
