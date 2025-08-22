package com.muqp.beatbeat.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.muqp.beatbeat.MusicApp
import com.muqp.beatbeat.databinding.ActivityMainBinding
import com.muqp.beatbeat.ui.utils.PermissionManager
import com.muqp.beatbeat.ui.utils.SystemBarsManager
import com.muqp.core_utils.has_dependencies.GetViewModelFactory
import com.muqp.core_utils.has_dependencies.HasDependencies
import javax.inject.Inject

class MainActivity @Inject constructor() : AppCompatActivity(), HasDependencies {

    private lateinit var binding: ActivityMainBinding
    private lateinit var systemBarsManager: SystemBarsManager
    private lateinit var permissionManager: PermissionManager

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    @Inject
    lateinit var getViewModelFactory: GetViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MusicApp.musicAppComponent.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        systemBarsManager = SystemBarsManager(window)
        permissionManager = PermissionManager(this, permissionLauncher)

        systemBarsManager.hideSystemBars()
        systemBarsManager.setupDisplayCutout(binding.root)

        permissionManager.checkAndRequestNeededPermissions()
    }

    override fun getViewModelFactory(): GetViewModelFactory {
        return getViewModelFactory
    }
}