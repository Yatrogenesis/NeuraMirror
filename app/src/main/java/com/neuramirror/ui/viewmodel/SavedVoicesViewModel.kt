package com.neuramirror.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neuramirror.data.model.SavedVoiceModel
import com.neuramirror.data.repository.VoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SavedVoicesViewModel @Inject constructor(
    private val voiceRepository: VoiceRepository
) : ViewModel() {

    private val _voiceModels = MutableLiveData<List<SavedVoiceModel>>(emptyList())
    val voiceModels: LiveData<List<SavedVoiceModel>> = _voiceModels
    
    private var originalList: List<SavedVoiceModel> = emptyList()
    private var currentSearchQuery: String = ""
    
    init {
        loadVoiceModels()
    }
    
    private fun loadVoiceModels() {
        viewModelScope.launch {
            try {
                voiceRepository.getAllVoiceModels().collectLatest { models ->
                    originalList = models
                    applyFilters()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error al cargar modelos de voz")
            }
        }
    }
    
    fun searchVoices(query: String) {
        currentSearchQuery = query
        applyFilters()
    }
    
    fun sortVoicesByDate() {
        val sorted = originalList.sortedByDescending { it.createdAt }
        applyFilters(sorted)
    }
    
    fun sortVoicesByUsage() {
        val sorted = originalList.sortedByDescending { it.lastUsedAt }
        applyFilters(sorted)
    }
    
    fun sortVoicesByName() {
        val sorted = originalList.sortedBy { it.name }
        applyFilters(sorted)
    }
    
    private fun applyFilters(list: List<SavedVoiceModel> = originalList) {
        var filteredList = list
        
        // Aplicar filtro de búsqueda si hay texto
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true) ||
                        it.tags.contains(currentSearchQuery, ignoreCase = true)
            }
        }
        
        _voiceModels.postValue(filteredList)
    }
    
    fun deleteVoiceModel(modelId: String) {
        viewModelScope.launch {
            try {
                voiceRepository.deleteVoiceModel(modelId)
                // No es necesario actualizar la lista, ya que el Flow la actualizará automáticamente
            } catch (e: Exception) {
                Timber.e(e, "Error al eliminar modelo de voz")
            }
        }
    }
}
