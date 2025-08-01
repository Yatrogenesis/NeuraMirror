package com.neuramirror.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un modelo de voz guardado en la base de datos.
 */
@Entity(tableName = "voice_models")
data class SavedVoiceModel(
    @PrimaryKey
    val id: String,
    
    /** Nombre personalizado del modelo */
    var name: String,
    
    /** Timestamp de creación (milisegundos) */
    val createdAt: Long,
    
    /** Timestamp de último uso (milisegundos) */
    var lastUsedAt: Long,
    
    /** Etiquetas opcionales */
    var tags: String = "",
    
    /** Notas personalizadas */
    var notes: String = ""
)
