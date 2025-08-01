package com.neuramirror.ui.saved

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.neuramirror.data.model.SavedVoiceModel
import com.neuramirror.databinding.ItemSavedVoiceBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SavedVoicesAdapter(
    private val onItemClick: (SavedVoiceModel) -> Unit,
    private val onOptionsClick: (SavedVoiceModel, View) -> Unit
) : ListAdapter<SavedVoiceModel, SavedVoicesAdapter.ViewHolder>(VoiceModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSavedVoiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val voiceModel = getItem(position)
        holder.bind(voiceModel)
    }
    
    inner class ViewHolder(private val binding: ItemSavedVoiceBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(voiceModel: SavedVoiceModel) {
            binding.tvVoiceName.text = voiceModel.name
            
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(voiceModel.createdAt))
            binding.tvCreatedDate.text = "Creada el $formattedDate"
            
            binding.root.setOnClickListener {
                onItemClick(voiceModel)
            }
            
            binding.btnOptions.setOnClickListener {
                onOptionsClick(voiceModel, it)
            }
        }
    }
    
    class VoiceModelDiffCallback : DiffUtil.ItemCallback<SavedVoiceModel>() {
        override fun areItemsTheSame(oldItem: SavedVoiceModel, newItem: SavedVoiceModel): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: SavedVoiceModel, newItem: SavedVoiceModel): Boolean {
            return oldItem == newItem
        }
    }
}
