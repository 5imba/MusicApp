package com.bogleo.musicapp.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.bogleo.musicapp.R
import com.bogleo.musicapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        checkPermission()
    }

    private fun checkPermission() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            return
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkPermission()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Request Permission")
                .setMessage("Allow us to fetch & show songs on your device")
                .setPositiveButton("Allow") { _, _ ->
                    checkPermission()
                }
                .setNegativeButton("Don't Allow") { dialogInterface, _ ->
                    Toast.makeText(this,
                        "You denied to fetch songs, set permission on settings to load your songs",
                        Toast.LENGTH_LONG).show()
                    dialogInterface.dismiss()
                }
                .show()
        }
    }
}