package com.neuramirror.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.neuramirror.R
import com.neuramirror.aion.AionProtocol
import com.neuramirror.aion.processor.AudioProcessor
import com.neuramirror.aion.processor.VoiceModel
import com.neuramirror.aion.processor.VoiceModelProcessor
import com.neuramirror.data.repository.VoiceRepository
import com.neuramirror.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val voiceRepository: VoiceRepository,
    private val audioProcessor: AudioProcessor
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val aionProtocol = AionProtocol.getInstance()
    
    // Directorios de archivos
    private val audioDir = File(context.filesDir, "audio")
    private val recordingsDir = File(audioDir, "recordings")
    private val generatedDir = File(audioDir, "generated")
    
    // Estado de grabación
    private val _isRecording = MutableLiveData<Boolean>(false)
    val isRecording: LiveData<Boolean> = _isRecording
    
    // Estado de procesamiento
    private val _isProcessing = MutableLiveData<Boolean>(false)
    val isProcessing: LiveData<Boolean> = _isProcessing
    
    // Mensaje de error (SingleLiveEvent para evitar múltiples observaciones)
    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> = _errorMessage
    
    // Mensaje de éxito
    private val _successMessage = SingleLiveEvent<String>()
    val successMessage: LiveData<String> = _successMessage
    
    // Archivo de grabación actual
    private var currentRecordingFile: File? = null
    
    // Modelo de voz actual
    private val _currentVoiceModel = MutableLiveData<VoiceModel?>()
    val currentVoiceModel: LiveData<VoiceModel?> = _currentVoiceModel
    
    // Texto a sintetizar
    private val _textToSynthesize = MutableLiveData<String>("")
    val textToSynthesize: LiveData<String> = _textToSynthesize
    
    // Archivo de audio generado
    private val _generatedAudioFile = MutableLiveData<File?>()
    val generatedAudioFile: LiveData<File?> = _generatedAudioFile
    
    // Parámetros emocionales
    private val _emotionParams = MutableLiveData<Map<String, Float>>(emptyMap())
    val emotionParams: LiveData<Map<String, Float>> = _emotionParams
    
    init {
        // Crear directorios necesarios
        audioDir.mkdirs()
        recordingsDir.mkdirs()
        generatedDir.mkdirs()
        
        // Inicializar procesador de audio
        viewModelScope.launch {
            try {
                audioProcessor.initialize(context)
            } catch (e: Exception) {
                Timber.e(e, "Error al inicializar procesador de audio")
                _errorMessage.postValue(context.getString(R.string.error_init_audio_processor))
            }
        }
    }
    
    /**
     * Acción cuando se otorgan los permisos necesarios.
     */
    fun onPermissionsGranted() {
        Timber.d("Permisos concedidos, listo para operar")
    }
    
    /**
     * Inicia la grabación de audio.
     */
    fun startRecording() {
        if (_isRecording.value == true) {
            Timber.d("Ya se está grabando, ignorando solicitud")
            return
        }
        
        viewModelScope.launch {
            try {
                // Crear archivo para la grabación
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val file = File(recordingsDir, "recording_$timestamp.wav")
                
                // Iniciar grabación
                _isRecording.postValue(true)
                val success = audioProcessor.recordAudio(file, 10000) // 10 segundos
                
                if (success) {
                    currentRecordingFile = file
                    _successMessage.postValue(context.getString(R.string.recording_complete))
                } else {
                    _errorMessage.postValue(context.getString(R.string.error_recording))
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Error al grabar audio")
                _errorMessage.postValue(context.getString(R.string.error_recording))
            } finally {
                _isRecording.postValue(false)
            }
        }
    }
    
    /**
     * Detiene la grabación de audio.
     */
    fun stopRecording() {
        if (_isRecording.value != true) {
            Timber.d("No se está grabando, ignorando solicitud")
            return
        }
        
        _isRecording.postValue(false)
        // La grabación se detendrá automáticamente por la duración en el método startRecording
    }
    
    /**
     * Procesa la grabación actual para generar un modelo de voz.
     */
    fun processRecording() {
        val recordingFile = currentRecordingFile
        if (recordingFile == null || !recordingFile.exists()) {
            _errorMessage.postValue(context.getString(R.string.error_no_recording))
            return
        }
        
        viewModelScope.launch {
            try {
                _isProcessing.postValue(true)
                
                // Extraer características de audio
                val audioFeatures = audioProcessor.extractFeatures(recordingFile)
                
                // Generar modelo de voz con protocolo AION
                val modelId = aionProtocol.processVoiceSample(recordingFile)
                
                // Cargar modelo generado
                val voiceModel = withContext(Dispatchers.IO) {
                    VoiceModelProcessor(File(context.filesDir, "aion_models"))
                        .loadVoiceModel(modelId)
                }
                
                if (voiceModel != null) {
                    _currentVoiceModel.postValue(voiceModel)
                    _successMessage.postValue(context.getString(R.string.voice_model_created))
                } else {
                    _errorMessage.postValue(context.getString(R.string.error_creating_model))
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Error al procesar grabación: ${e.message}")
                _errorMessage.postValue(context.getString(R.string.error_processing))
            } finally {
                _isProcessing.postValue(false)
            }
        }
    }
    
    /**
     * Sintetiza voz a partir del texto ingresado.
     */
    fun synthesizeVoice(text: String) {
        val voiceModel = _currentVoiceModel.value
        if (voiceModel == null) {
            _errorMessage.postValue(context.getString(R.string.error_no_voice_model))
            return
        }
        
        if (text.isBlank()) {
            _errorMessage.postValue(context.getString(R.string.error_empty_text))
            return
        }
        
        viewModelScope.launch {
            try {
                _isProcessing.postValue(true)
                
                // Crear archivo para el audio generado
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val outputFile = File(generatedDir, "generated_$timestamp.wav")
                
                // Obtener parámetros emocionales actuales
                val emotionParams = _emotionParams.value ?: emptyMap()
                
                // Sintetizar voz
                val success = aionProtocol.synthesizeVoice(
                    text = text,
                    voiceModelId = voiceModel.id,
                    outputFile = outputFile,
                    emotionParams = emotionParams
                )
                
                if (success) {
                    _generatedAudioFile.postValue(outputFile)
                    _successMessage.postValue(context.getString(R.string.voice_generated))
                } else {
                    _errorMessage.postValue(context.getString(R.string.error_synthesizing))
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Error al sintetizar voz: ${e.message}")
                _errorMessage.postValue(context.getString(R.string.error_synthesizing))
            } finally {
                _isProcessing.postValue(false)
            }
        }
    }
    
    /**
     * Actualiza el texto a sintetizar.
     */
    fun updateTextToSynthesize(text: String) {
        _textToSynthesize.postValue(text)
    }
    
    /**
     * Actualiza los parámetros emocionales.
     */
    fun updateEmotionParams(params: Map<String, Float>) {
        _emotionParams.postValue(params)
    }
    
    /**
     * Selecciona un archivo de audio para procesar.
     */
    fun selectAudioFile(file: File) {
        if (!file.exists()) {
            _errorMessage.postValue(context.getString(R.string.error_file_not_found))
            return
        }
        
        currentRecordingFile = file
        _successMessage.postValue(context.getString(R.string.file_selected))
    }
    
    /**
     * Guarda el modelo de voz actual con un nombre personalizado.
     */
    fun saveVoiceModel(name: String) {
        val model = _currentVoiceModel.value ?: return
        
        viewModelScope.launch {
            try {
                // Guardar en repositorio
                voiceRepository.saveVoiceModel(model.id, name)
                _successMessage.postValue(context.getString(R.string.model_saved))
            } catch (e: Exception) {
                Timber.e(e, "Error al guardar modelo: ${e.message}")
                _errorMessage.postValue(context.getString(R.string.error_saving_model))
            }
        }
    }
    
    /**
     * Reproduce el audio original grabado.
     */
    fun playOriginalAudio() {
        currentRecordingFile?.let { file ->
            if (file.exists()) {
                // Implementación de reproducción de audio
                // Podría utilizar MediaPlayer o ExoPlayer
            } else {
                _errorMessage.postValue(context.getString(R.string.error_file_not_found))
            }
        } ?: run {
            _errorMessage.postValue(context.getString(R.string.error_no_recording))
        }
    }
    
    /**
     * Reproduce el audio generado.
     */
    fun playGeneratedAudio() {
        _generatedAudioFile.value?.let { file ->
            if (file.exists()) {
                // Implementación de reproducción de audio
                // Podría utilizar MediaPlayer o ExoPlayer
            } else {
                _errorMessage.postValue(context.getString(R.string.error_file_not_found))
            }
        } ?: run {
            _errorMessage.postValue(context.getString(R.string.error_no_generated_audio))
        }
    }
    
    /**
     * Exporta el audio generado.
     */
    fun exportGeneratedAudio(format: String): File? {
        val sourceFile = _generatedAudioFile.value ?: return null
        
        if (!sourceFile.exists()) {
            _errorMessage.postValue(context.getString(R.string.error_file_not_found))
            return null
        }
        
        try {
            // Crear archivo de destino con el formato especificado
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val exportFile = File(context.getExternalFilesDir(null), 
                "neuramirror_export_$timestamp.$format")
            
            // Convertir formato si es necesario
            // Implementación simplificada, en un caso real usaríamos FFmpeg
            sourceFile.copyTo(exportFile, overwrite = true)
            
            _successMessage.postValue(
                context.getString(R.string.success_export, exportFile.absolutePath)
            )
            
            return exportFile
            
        } catch (e: Exception) {
            Timber.e(e, "Error al exportar audio: ${e.message}")
            _errorMessage.postValue(context.getString(R.string.error_exporting))
            return null
        }
    }
    
    /**
     * Limpia el estado actual después de mostrar un mensaje de error.
     */
    fun onErrorMessageShown() {
        _errorMessage.value = ""
    }
    
    /**
     * Limpia el estado actual después de mostrar un mensaje de éxito.
     */
    fun onSuccessMessageShown() {
        _successMessage.value = ""
    }
}
