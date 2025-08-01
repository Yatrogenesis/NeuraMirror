package com.neuramirror.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.neuramirror.data.model.SavedVoiceModel

/**
 * Base de datos principal de la aplicación.
 */
@Database(
    entities = [SavedVoiceModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Proporciona acceso al DAO de modelos de voz.
     */
    abstract fun voiceModelDao(): VoiceModelDao
    
    companion object {
        const val DATABASE_NAME = "neuramirror_db"
    }
}
