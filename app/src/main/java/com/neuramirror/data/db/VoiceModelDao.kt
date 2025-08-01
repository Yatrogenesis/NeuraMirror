package com.neuramirror.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.neuramirror.data.model.SavedVoiceModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con modelos de voz guardados.
 */
@Dao
interface VoiceModelDao {

    /**
     * Inserta un nuevo modelo de voz.
     * Si ya existe un modelo con el mismo ID, lo reemplaza.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceModel(voiceModel: SavedVoiceModel)
    
    /**
     * Actualiza un modelo de voz existente.
     */
    @Update
    suspend fun updateVoiceModel(voiceModel: SavedVoiceModel)
    
    /**
     * Elimina un modelo de voz.
     */
    @Delete
    suspend fun deleteVoiceModel(voiceModel: SavedVoiceModel)
    
    /**
     * Elimina un modelo de voz por ID.
     */
    @Query("DELETE FROM voice_models WHERE id = :modelId")
    suspend fun deleteVoiceModelById(modelId: String)
    
    /**
     * Obtiene un modelo de voz por ID.
     */
    @Query("SELECT * FROM voice_models WHERE id = :modelId")
    suspend fun getVoiceModelById(modelId: String): SavedVoiceModel?
    
    /**
     * Obtiene todos los modelos de voz ordenados por fecha de creación (más recientes primero).
     */
    @Query("SELECT * FROM voice_models ORDER BY createdAt DESC")
    fun getAllVoiceModels(): Flow<List<SavedVoiceModel>>
    
    /**
     * Obtiene todos los modelos de voz ordenados por fecha de último uso.
     */
    @Query("SELECT * FROM voice_models ORDER BY lastUsedAt DESC")
    fun getVoiceModelsByLastUsed(): Flow<List<SavedVoiceModel>>
    
    /**
     * Busca modelos de voz por nombre.
     */
    @Query("SELECT * FROM voice_models WHERE name LIKE '%' || :query || '%'")
    fun searchVoiceModelsByName(query: String): Flow<List<SavedVoiceModel>>
    
    /**
     * Busca modelos de voz por etiqueta.
     */
    @Query("SELECT * FROM voice_models WHERE tags LIKE '%' || :tag || '%'")
    fun getVoiceModelsByTag(tag: String): Flow<List<SavedVoiceModel>>
    
    /**
     * Cuenta el número total de modelos guardados.
     */
    @Query("SELECT COUNT(*) FROM voice_models")
    suspend fun getVoiceModelCount(): Int
}
