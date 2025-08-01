package com.neuramirror.data.repository

import com.neuramirror.data.db.VoiceModelDao
import com.neuramirror.data.model.SavedVoiceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para la gestión de modelos de voz guardados.
 */
@Singleton
class VoiceRepository @Inject constructor(
    private val voiceModelDao: VoiceModelDao,
    private val modelsDirectory: File
) {

    /**
     * Obtiene todos los modelos de voz guardados.
     */
    fun getAllVoiceModels(): Flow<List<SavedVoiceModel>> {
        return voiceModelDao.getAllVoiceModels()
    }
    
    /**
     * Obtiene un modelo de voz por ID.
     */
    suspend fun getVoiceModelById(id: String): SavedVoiceModel? = withContext(Dispatchers.IO) {
        return@withContext voiceModelDao.getVoiceModelById(id)
    }
    
    /**
     * Guarda un modelo de voz con un nombre personalizado.
     */
    suspend fun saveVoiceModel(modelId: String, name: String) = withContext(Dispatchers.IO) {
        try {
            // Verificar si el modelo existe en el sistema de archivos
            val modelDir = File(modelsDirectory, modelId)
            if (!modelDir.exists() || !modelDir.isDirectory) {
                throw IllegalArgumentException("Modelo no encontrado: $modelId")
            }
            
            // Crear entidad para base de datos
            val savedModel = SavedVoiceModel(
                id = modelId,
                name = name,
                createdAt = System.currentTimeMillis(),
                lastUsedAt = System.currentTimeMillis()
            )
            
            // Guardar en base de datos
            voiceModelDao.insertVoiceModel(savedModel)
            
            Timber.d("Modelo guardado: $modelId - $name")
            
        } catch (e: Exception) {
            Timber.e(e, "Error al guardar modelo de voz: ${e.message}")
            throw e
        }
    }
    
    /**
     * Actualiza el nombre de un modelo de voz guardado.
     */
    suspend fun updateVoiceModelName(modelId: String, newName: String) = withContext(Dispatchers.IO) {
        try {
            val model = voiceModelDao.getVoiceModelById(modelId)
                ?: throw IllegalArgumentException("Modelo no encontrado: $modelId")
            
            // Actualizar nombre
            model.name = newName
            
            // Actualizar en base de datos
            voiceModelDao.updateVoiceModel(model)
            
            Timber.d("Nombre de modelo actualizado: $modelId - $newName")
            
        } catch (e: Exception) {
            Timber.e(e, "Error al actualizar nombre de modelo: ${e.message}")
            throw e
        }
    }
    
    /**
     * Actualiza la fecha de último uso de un modelo.
     */
    suspend fun updateLastUsedTime(modelId: String) = withContext(Dispatchers.IO) {
        try {
            val model = voiceModelDao.getVoiceModelById(modelId)
                ?: throw IllegalArgumentException("Modelo no encontrado: $modelId")
            
            // Actualizar timestamp
            model.lastUsedAt = System.currentTimeMillis()
            
            // Actualizar en base de datos
            voiceModelDao.updateVoiceModel(model)
            
        } catch (e: Exception) {
            Timber.e(e, "Error al actualizar tiempo de uso: ${e.message}")
            throw e
        }
    }
    
    /**
     * Elimina un modelo de voz guardado.
     */
    suspend fun deleteVoiceModel(modelId: String) = withContext(Dispatchers.IO) {
        try {
            // Eliminar de base de datos
            voiceModelDao.deleteVoiceModelById(modelId)
            
            // Eliminar archivos del modelo
            val modelDir = File(modelsDirectory, modelId)
            if (modelDir.exists() && modelDir.isDirectory) {
                modelDir.listFiles()?.forEach { it.delete() }
                modelDir.delete()
            }
            
            Timber.d("Modelo eliminado: $modelId")
            
        } catch (e: Exception) {
            Timber.e(e, "Error al eliminar modelo: ${e.message}")
            throw e
        }
    }
}
