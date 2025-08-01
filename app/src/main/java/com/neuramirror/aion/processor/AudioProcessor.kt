package com.neuramirror.aion.processor

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import timber.log.Timber
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Procesador de audio para el protocolo AION.
 * Extrae características de audio y prepara datos para el modelado de voz.
 */
class AudioProcessor {

    private var initialized = false
    private lateinit var context: Context
    private var tfliteInterpreter: Interpreter? = null
    private val sampleRate = 22050 // Hz
    private val frameLength = 512 // muestras
    private val hopLength = 128 // muestras
    private val melBins = 80
    
    companion object {
        private const val TAG = "AudioProcessor"
        private const val FEATURE_EXTRACTOR_MODEL = "aion_audio_feature_extractor.tflite"
    }
    
    /**
     * Inicializa el procesador de audio.
     */
    fun initialize(context: Context) {
        if (initialized) {
            Timber.d("AudioProcessor ya inicializado")
            return
        }
        
        this.context = context.applicationContext
        
        // Cargar modelo TFLite para extracción de características
        try {
            val modelFile = loadModelFile()
            val options = Interpreter.Options()
            tfliteInterpreter = Interpreter(modelFile, options)
            initialized = true
            Timber.d("AudioProcessor inicializado correctamente")
        } catch (e: Exception) {
            Timber.e(e, "Error al inicializar AudioProcessor")
        }
    }
    
    /**
     * Extrae características de un archivo de audio.
     * 
     * @param audioFile Archivo de audio a procesar
     * @return Mapa con características extraídas
     */
    suspend fun extractFeatures(audioFile: File): Map<String, Any> = withContext(Dispatchers.IO) {
        checkInitialized()
        
        try {
            // Cargar archivo de audio
            val audioData = loadAudioFile(audioFile)
            
            // Preprocesar audio (normalización, recorte, etc.)
            val processedAudio = preprocessAudio(audioData)
            
            // Extraer características usando el modelo TFLite
            val features = extractFeaturesWithTFLite(processedAudio)
            
            // Extraer características adicionales específicas de AION
            val aionFeatures = extractAionSpecificFeatures(processedAudio)
            
            // Combinar todas las características
            val allFeatures = features.toMutableMap()
            allFeatures.putAll(aionFeatures)
            
            Timber.d("Características extraídas con éxito: ${allFeatures.keys}")
            
            return@withContext allFeatures
            
        } catch (e: Exception) {
            Timber.e(e, "Error al extraer características de audio")
            return@withContext emptyMap<String, Any>()
        }
    }
    
    /**
     * Graba audio desde el micrófono.
     * 
     * @param outputFile Archivo de salida para la grabación
     * @param durationMs Duración de la grabación en milisegundos
     * @return Éxito de la operación
     */
    suspend fun recordAudio(outputFile: File, durationMs: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val bufferSize = AudioRecord.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            
            val audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            
            if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
                Timber.e("Error al inicializar AudioRecord")
                return@withContext false
            }
            
            val buffer = ShortArray(bufferSize)
            outputFile.outputStream().use { outputStream ->
                audioRecord.startRecording()
                
                // Calcular número total de lecturas basado en la duración
                val totalReads = (durationMs * sampleRate) / (1000 * bufferSize)
                
                for (i in 0 until totalReads) {
                    val read = audioRecord.read(buffer, 0, bufferSize)
                    if (read > 0) {
                        // Convertir shorts a bytes y escribir en archivo
                        val byteBuffer = ByteBuffer.allocate(read * 2)
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                        
                        for (j in 0 until read) {
                            byteBuffer.putShort(buffer[j])
                        }
                        
                        outputStream.write(byteBuffer.array())
                    }
                }
                
                audioRecord.stop()
                audioRecord.release()
            }
            
            return@withContext true
            
        } catch (e: Exception) {
            Timber.e(e, "Error al grabar audio")
            return@withContext false
        }
    }
    
    /**
     * Carga archivo de modelo TFLite.
     */
    private fun loadModelFile(): ByteBuffer {
        val assetManager = context.assets
        val modelPath = FEATURE_EXTRACTOR_MODEL
        
        return assetManager.openFd(modelPath).use { fileDescriptor ->
            val inputStream = fileDescriptor.createInputStream()
            val modelBuffer = ByteBuffer.allocateDirect(fileDescriptor.length.toInt())
            val channels = inputStream.channel
            channels.position(fileDescriptor.startOffset)
            channels.read(modelBuffer)
            modelBuffer.rewind()
            modelBuffer
        }
    }
    
    /**
     * Carga y decodifica un archivo de audio.
     */
    private fun loadAudioFile(audioFile: File): FloatArray {
        // Implementación real utilizaría FFmpeg o similar
        // Este es un código simplificado para el ejemplo
        
        val command = arrayOf(
            "ffmpeg", 
            "-i", audioFile.absolutePath,
            "-ar", sampleRate.toString(),
            "-ac", "1",  // mono
            "-f", "f32le",  // 32-bit float PCM
            "-"  // output to pipe
        )
        
        val process = ProcessBuilder(*command)
            .redirectErrorStream(true)
            .start()
        
        val outputBytes = process.inputStream.readBytes()
        val samples = outputBytes.size / 4  // 4 bytes per float
        
        val floatBuffer = ByteBuffer.wrap(outputBytes).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer()
        val audioData = FloatArray(samples)
        floatBuffer.get(audioData)
        
        return audioData
    }
    
    /**
     * Preprocesa los datos de audio para normalización y preparación.
     */
    private fun preprocessAudio(audioData: FloatArray): FloatArray {
        // Normalizar audio
        val normalizedAudio = normalize(audioData)
        
        // Recortar silencio
        val trimmedAudio = trimSilence(normalizedAudio)
        
        // Resamplear si es necesario
        val resampledAudio = if (sampleRate != 22050) {
            resample(trimmedAudio, sampleRate, 22050)
        } else {
            trimmedAudio
        }
        
        return resampledAudio
    }
    
    /**
     * Extrae características usando el modelo TFLite.
     */
    private fun extractFeaturesWithTFLite(audioData: FloatArray): Map<String, Any> {
        val interpreter = tfliteInterpreter ?: return emptyMap()
        
        // Preparar datos de entrada
        val inputBuffer = FloatBuffer.allocate(audioData.size)
        inputBuffer.put(audioData)
        inputBuffer.rewind()
        
        // Preparar buffer de salida (ajustar tamaños según el modelo)
        val outputSize = melBins * (audioData.size / hopLength)
        val outputBuffer = FloatBuffer.allocate(outputSize)
        
        // Ejecutar inferencia
        val inputs = mapOf(0 to inputBuffer)
        val outputs = mapOf(0 to outputBuffer)
        
        interpreter.runForMultipleInputsOutputs(inputs, outputs)
        
        // Procesar salida
        val featureMap = mutableMapOf<String, Any>()
        
        // Convertir buffer a array para uso posterior
        val melSpectrogram = FloatArray(outputSize)
        outputBuffer.rewind()
        outputBuffer.get(melSpectrogram)
        
        // Guardar características
        featureMap["mel_spectrogram"] = melSpectrogram
        featureMap["sample_rate"] = sampleRate
        featureMap["n_fft"] = frameLength
        featureMap["hop_length"] = hopLength
        featureMap["n_mels"] = melBins
        
        return featureMap
    }
    
    /**
     * Extrae características específicas del protocolo AION.
     */
    private fun extractAionSpecificFeatures(audioData: FloatArray): Map<String, Any> {
        val aionFeatures = mutableMapOf<String, Any>()
        
        // Extracción de pitch (F0)
        val pitchValues = extractPitch(audioData)
        aionFeatures["pitch_values"] = pitchValues
        
        // Extracción de contorno de energía
        val energyContour = extractEnergyContour(audioData)
        aionFeatures["energy_contour"] = energyContour
        
        // Extracción de características de timbre
        val timbreFeatures = extractTimbreFeatures(audioData)
        aionFeatures["timbre_features"] = timbreFeatures
        
        // Análisis de emoción
        val emotionVector = analyzeEmotion(audioData)
        aionFeatures["emotion_vector"] = emotionVector
        
        return aionFeatures
    }
    
    /**
     * Normaliza el audio para que tenga un rango de amplitud uniforme.
     */
    private fun normalize(audioData: FloatArray): FloatArray {
        val result = FloatArray(audioData.size)
        
        var maxAbs = 0.0f
        for (sample in audioData) {
            val abs = Math.abs(sample)
            if (abs > maxAbs) {
                maxAbs = abs
            }
        }
        
        // Evitar división por cero
        if (maxAbs < 1e-10) {
            return audioData.copyOf()
        }
        
        // Normalizar a rango [-1, 1]
        for (i in audioData.indices) {
            result[i] = audioData[i] / maxAbs
        }
        
        return result
    }
    
    /**
     * Recorta el silencio al inicio y final de la muestra de audio.
     */
    private fun trimSilence(audioData: FloatArray, threshold: Float = 0.01f): FloatArray {
        var startIdx = 0
        var endIdx = audioData.size - 1
        
        // Encontrar índice de inicio (primer sample sobre el threshold)
        for (i in audioData.indices) {
            if (Math.abs(audioData[i]) > threshold) {
                startIdx = Math.max(0, i - 100)  // Mantener un poco de contexto
                break
            }
        }
        
        // Encontrar índice de fin (último sample sobre el threshold)
        for (i in audioData.indices.reversed()) {
            if (Math.abs(audioData[i]) > threshold) {
                endIdx = Math.min(audioData.size - 1, i + 100)  // Mantener un poco de contexto
                break
            }
        }
        
        // Si startIdx >= endIdx, es todo silencio
        if (startIdx >= endIdx) {
            return FloatArray(0)
        }
        
        return audioData.copyOfRange(startIdx, endIdx + 1)
    }
    
    /**
     * Resamplea el audio a una nueva frecuencia de muestreo.
     */
    private fun resample(audioData: FloatArray, fromSampleRate: Int, toSampleRate: Int): FloatArray {
        // Si las tasas son iguales, no hacer nada
        if (fromSampleRate == toSampleRate) {
            return audioData.copyOf()
        }
        
        // Calcular nuevo tamaño
        val ratio = toSampleRate.toDouble() / fromSampleRate.toDouble()
        val newSize = (audioData.size * ratio).toInt()
        val result = FloatArray(newSize)
        
        // Algoritmo simple de interpolación lineal
        for (i in 0 until newSize) {
            val exactIdx = i / ratio
            val lowerIdx = exactIdx.toInt()
            val upperIdx = Math.min(lowerIdx + 1, audioData.size - 1)
            val fraction = exactIdx - lowerIdx
            
            result[i] = (1 - fraction) * audioData[lowerIdx] + fraction * audioData[upperIdx]
        }
        
        return result
    }
    
    /**
     * Extrae el pitch (F0) del audio.
     */
    private fun extractPitch(audioData: FloatArray): FloatArray {
        // Implementar algoritmo YIN o similar para detección de pitch
        // Esta es una implementación simplificada
        
        val windowSize = 1024
        val hopSize = 256
        val numFrames = (audioData.size - windowSize) / hopSize + 1
        val pitchValues = FloatArray(numFrames)
        
        // Simplificación: detección básica de periodicidad
        for (i in 0 until numFrames) {
            val start = i * hopSize
            val end = start + windowSize
            
            // Implementación simplificada de autocorrelación
            var maxCorrelation = 0.0f
            var bestLag = 0
            
            for (lag in 50 until 500) {  // Rango aproximado 100-500Hz
                var correlation = 0.0f
                
                for (j in 0 until windowSize - lag) {
                    correlation += audioData[start + j] * audioData[start + j + lag]
                }
                
                if (correlation > maxCorrelation) {
                    maxCorrelation = correlation
                    bestLag = lag
                }
            }
            
            // Convertir lag a frecuencia (Hz)
            pitchValues[i] = if (bestLag > 0) sampleRate.toFloat() / bestLag else 0f
        }
        
        return pitchValues
    }
    
    /**
     * Extrae el contorno de energía del audio.
     */
    private fun extractEnergyContour(audioData: FloatArray): FloatArray {
        val windowSize = 1024
        val hopSize = 256
        val numFrames = (audioData.size - windowSize) / hopSize + 1
        val energyContour = FloatArray(numFrames)
        
        for (i in 0 until numFrames) {
            val start = i * hopSize
            var energy = 0.0f
            
            for (j in 0 until windowSize) {
                val sample = audioData[start + j]
                energy += sample * sample
            }
            
            energyContour[i] = energy / windowSize
        }
        
        return energyContour
    }
    
    /**
     * Extrae características de timbre del audio.
     */
    private fun extractTimbreFeatures(audioData: FloatArray): Map<String, FloatArray> {
        val timbreFeatures = mutableMapOf<String, FloatArray>()
        
        // Implementación simplificada de MFCC
        // En un caso real, usaríamos una biblioteca como JSFX
        
        val numCoefficients = 13
        val windowSize = 1024
        val hopSize = 256
        val numFrames = (audioData.size - windowSize) / hopSize + 1
        
        val mfccs = Array(numCoefficients) { FloatArray(numFrames) }
        
        // Simulación de cálculo de MFCCs
        for (i in 0 until numFrames) {
            for (j in 0 until numCoefficients) {
                // Valor simulado basado en energía local
                mfccs[j][i] = (Math.random() * 2 - 1).toFloat()
            }
        }
        
        // Convertir array 2D a mapa de features
        for (i in 0 until numCoefficients) {
            timbreFeatures["mfcc_$i"] = mfccs[i]
        }
        
        return timbreFeatures
    }
    
    /**
     * Analiza el componente emocional del audio.
     */
    private fun analyzeEmotion(audioData: FloatArray): FloatArray {
        // Implementación simplificada
        // En un caso real, usaríamos un modelo específico para clasificación de emociones
        
        // Vector de 5 dimensiones para emociones básicas:
        // [neutral, happy, sad, angry, surprised]
        val emotionVector = FloatArray(5)
        
        // Ejemplo simulado (valores aleatorios)
        var sum = 0.0f
        for (i in emotionVector.indices) {
            emotionVector[i] = Math.random().toFloat()
            sum += emotionVector[i]
        }
        
        // Normalizar a probabilidades
        for (i in emotionVector.indices) {
            emotionVector[i] /= sum
        }
        
        return emotionVector
    }
    
    /**
     * Verifica la inicialización.
     */
    private fun checkInitialized() {
        if (!initialized) {
            throw IllegalStateException("AudioProcessor no inicializado. Llame a initialize() primero.")
        }
    }
}
