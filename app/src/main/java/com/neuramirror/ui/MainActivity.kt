package com.neuramirror.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.neuramirror.R
import com.neuramirror.databinding.ActivityMainBinding
import com.neuramirror.service.AudioProcessingService
import com.neuramirror.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    
    private val viewModel: MainViewModel by viewModels()
    
    // Launcher para solicitar permisos de grabación
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        
        if (allGranted) {
            viewModel.onPermissionsGranted()
            Timber.d("Todos los permisos concedidos")
        } else {
            showPermissionDeniedMessage()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        // Configurar navegación
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.savedVoicesFragment, R.id.helpFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)
        
        // Configurar FAB
        setupFab()
        
        // Iniciar servicio de procesamiento de audio
        startAudioProcessingService()
        
        // Observar cambios en el ViewModel
        observeViewModel()
        
        // Verificar permisos
        checkPermissions()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                navController.navigate(R.id.settingsFragment)
                true
            }
            R.id.menu_about -> {
                // Mostrar diálogo de acerca de
                true
            }
            R.id.menu_help -> {
                navController.navigate(R.id.helpFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    private fun setupFab() {
        val fab: FloatingActionButton = binding.fab
        
        // Cambiar comportamiento del FAB según la pantalla actual
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    fab.visibility = View.VISIBLE
                    updateFabForRecordingState()
                }
                R.id.savedVoicesFragment -> {
                    fab.visibility = View.VISIBLE
                    fab.setImageResource(R.drawable.ic_add)
                    fab.setOnClickListener {
                        navController.navigate(R.id.action_savedVoices_to_create)
                    }
                }
                else -> {
                    fab.visibility = View.GONE
                }
            }
        }
    }
    
    private fun updateFabForRecordingState() {
        val fab: FloatingActionButton = binding.fab
        val isRecording = viewModel.isRecording.value ?: false
        
        if (isRecording) {
            fab.setImageResource(R.drawable.ic_stop)
            fab.setOnClickListener {
                viewModel.stopRecording()
            }
        } else {
            fab.setImageResource(R.drawable.ic_record)
            fab.setOnClickListener {
                if (checkPermissions()) {
                    viewModel.startRecording()
                }
            }
        }
    }
    
    private fun observeViewModel() {
        // Observar estado de grabación
        viewModel.isRecording.observe(this) { isRecording ->
            updateFabForRecordingState()
            binding.recordingIndicator.visibility = if (isRecording) View.VISIBLE else View.GONE
            binding.recordingText.visibility = if (isRecording) View.VISIBLE else View.GONE
            binding.recordingText.text = getString(R.string.record_sample)
        }
        
        // Observar estado de procesamiento
        viewModel.isProcessing.observe(this) { isProcessing ->
            binding.progressOverlay.visibility = if (isProcessing) View.VISIBLE else View.GONE
        }
        
        // Observar mensajes de error
        viewModel.errorMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorMessageShown()
            }
        }
        
        // Observar mensajes de éxito
        viewModel.successMessage.observe(this) { message ->
            if (message.isNotEmpty()) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                viewModel.onSuccessMessageShown()
            }
        }
    }
    
    private fun startAudioProcessingService() {
        val serviceIntent = Intent(this, AudioProcessingService::class.java)
        startForegroundService(serviceIntent)
    }
    
    private fun checkPermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        
        if (!allGranted) {
            requestPermissionLauncher.launch(permissions)
            return false
        }
        
        return true
    }
    
    private fun showPermissionDeniedMessage() {
        Snackbar.make(
            binding.root,
            R.string.permission_denied_record_audio,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.action_ok) {
            // Abrir configuración de la aplicación
        }.show()
    }
}
