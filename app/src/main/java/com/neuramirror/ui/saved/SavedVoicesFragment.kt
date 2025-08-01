package com.neuramirror.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.neuramirror.R
import com.neuramirror.databinding.FragmentSavedVoicesBinding
import com.neuramirror.ui.viewmodel.SavedVoicesViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SavedVoicesFragment : Fragment() {

    private var _binding: FragmentSavedVoicesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: SavedVoicesViewModel by viewModels()
    private lateinit var adapter: SavedVoicesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedVoicesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupUI()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        adapter = SavedVoicesAdapter(
            onItemClick = { voiceModel ->
                // Navegar a detalles de voz
                val action = SavedVoicesFragmentDirections
                    .actionSavedVoicesToVoiceDetail(voiceModel.id)
                findNavController().navigate(action)
            },
            onOptionsClick = { voiceModel, view ->
                // Mostrar menú de opciones
                showOptionsMenu(voiceModel, view)
            }
        )
        
        binding.rvSavedVoices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavedVoices.adapter = adapter
    }
    
    private fun setupUI() {
        // Configurar búsqueda
        binding.etSearchVoices.setOnEditorActionListener { v, _, _ ->
            val query = v.text.toString()
            viewModel.searchVoices(query)
            true
        }
        
        // Configurar filtros
        binding.chipFilterNewest.setOnClickListener {
            viewModel.sortVoicesByDate()
        }
        
        binding.chipFilterMostUsed.setOnClickListener {
            viewModel.sortVoicesByUsage()
        }
        
        binding.chipFilterAlphabetical.setOnClickListener {
            viewModel.sortVoicesByName()
        }
        
        // Configurar botón de crear voz
        binding.btnCreateVoice.setOnClickListener {
            findNavController().navigate(R.id.action_savedVoices_to_create)
        }
    }
    
    private fun observeViewModel() {
        // Observar lista de voces guardadas
        viewModel.voiceModels.observe(viewLifecycleOwner) { models ->
            Timber.d("Modelos cargados: ${models.size}")
            adapter.submitList(models)
            
            // Actualizar visibilidad del estado vacío
            val isEmpty = models.isEmpty()
            binding.rvSavedVoices.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }
    
    private fun showOptionsMenu(voiceModel: com.neuramirror.data.model.SavedVoiceModel, anchor: View) {
        // Implementar menú de opciones (usar PopupMenu)
        Timber.d("Mostrar opciones para: ${voiceModel.name}")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
