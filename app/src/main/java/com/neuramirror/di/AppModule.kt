package com.neuramirror.di

import android.content.Context
import androidx.room.Room
import com.neuramirror.aion.processor.AudioProcessor
import com.neuramirror.data.db.AppDatabase
import com.neuramirror.data.db.VoiceModelDao
import com.neuramirror.data.repository.VoiceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

/**
 * Módulo de Dagger Hilt para proporcionar dependencias a nivel de aplicación.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Proporciona la instancia de base de datos.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // En caso de cambio de esquema, recrear base de datos
        .build()
    }
    
    /**
     * Proporciona el DAO de modelos de voz.
     */
    @Provides
    @Singleton
    fun provideVoiceModelDao(appDatabase: AppDatabase): VoiceModelDao {
        return appDatabase.voiceModelDao()
    }
    
    /**
     * Proporciona el directorio para los modelos de voz.
     */
    @Provides
    @Singleton
    fun provideModelsDirectory(@ApplicationContext context: Context): File {
        return File(context.filesDir, "aion_models").apply { mkdirs() }
    }
    
    /**
     * Proporciona el repositorio de voces.
     */
    @Provides
    @Singleton
    fun provideVoiceRepository(
        voiceModelDao: VoiceModelDao,
        modelsDirectory: File
    ): VoiceRepository {
        return VoiceRepository(voiceModelDao, modelsDirectory)
    }
    
    /**
     * Proporciona el procesador de audio.
     */
    @Provides
    @Singleton
    fun provideAudioProcessor(): AudioProcessor {
        return AudioProcessor()
    }
}
