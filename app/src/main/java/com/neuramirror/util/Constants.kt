package com.neuramirror.util

/**
 * Constantes utilizadas en toda la aplicación.
 */
object Constants {

    // Configuraciones generales
    const val DEFAULT_SAMPLE_RATE = 22050
    const val DEFAULT_FRAME_LENGTH = 512
    const val DEFAULT_HOP_LENGTH = 128
    const val DEFAULT_MEL_BINS = 80
    
    // Duración mínima de grabación en milisegundos
    const val MIN_RECORDING_DURATION_MS = 5000
    
    // Duración máxima de grabación en milisegundos
    const val MAX_RECORDING_DURATION_MS = 30000
    
    // Modos de procesamiento
    const val PROCESSING_MODE_CLOUD = "cloud"
    const val PROCESSING_MODE_LOCAL = "local"
    
    // Valores máximos para síntesis
    const val MAX_TEXT_LENGTH = 5000
    
    // Nombres de directorios
    const val MODELS_DIR = "aion_models"
    const val AUDIO_DIR = "audio"
    const val RECORDINGS_DIR = "recordings"
    const val GENERATED_DIR = "generated"
    
    // Emociones disponibles
    val AVAILABLE_EMOTIONS = listOf(
        "neutral",
        "happy",
        "sad",
        "angry",
        "surprised"
    )
    
    // Idiomas soportados
    val SUPPORTED_LANGUAGES = mapOf(
        "es-MX" to "Español (México)",
        "es-ES" to "Español (España)",
        "en-US" to "Inglés (EE.UU.)",
        "en-GB" to "Inglés (Reino Unido)",
        "fr-FR" to "Francés",
        "de-DE" to "Alemán",
        "it-IT" to "Italiano",
        "pt-BR" to "Portugués (Brasil)",
        "zh-CN" to "Chino (Mandarín)",
        "ja-JP" to "Japonés",
        "ko-KR" to "Coreano",
        "ru-RU" to "Ruso",
        "ar-SA" to "Árabe"
    )
    
    // Formatos de exportación
    val EXPORT_FORMATS = listOf(
        "wav",
        "mp3",
        "ogg",
        "flac"
    )
    
    // Intents
    const val ACTION_START_RECORDING = "com.neuramirror.action.START_RECORDING"
    const val ACTION_STOP_RECORDING = "com.neuramirror.action.STOP_RECORDING"
    const val ACTION_PROCESS_RECORDING = "com.neuramirror.action.PROCESS_RECORDING"
    const val ACTION_SYNTHESIZE_VOICE = "com.neuramirror.action.SYNTHESIZE_VOICE"
    
    // Extra keys
    const val EXTRA_VOICE_MODEL_ID = "voice_model_id"
    const val EXTRA_TEXT_TO_SYNTHESIZE = "text_to_synthesize"
    const val EXTRA_OUTPUT_FILE_PATH = "output_file_path"
    const val EXTRA_EMOTION_PARAMS = "emotion_params"
    
    // Tiempos de espera de red (ms)
    const val NETWORK_TIMEOUT_MS = 30000
    
    // Configuración de cache
    const val CACHE_SIZE_MB = 50
    
    // URLs y endpoints
    const val MINIMAX_API_BASE_URL = "https://api.minimaxi.chat/v1/"
    const val VOICE_CLONING_ENDPOINT = "voice-cloning"
    const val TEXT_TO_SPEECH_ENDPOINT = "text-to-speech"
}
