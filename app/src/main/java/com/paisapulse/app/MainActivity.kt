package com.paisapulse.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.paisapulse.app.ui.dashboard.DashboardScreen
import com.paisapulse.app.ui.dashboard.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint
// ... your existing imports
import android.widget.Button // ADD THIS IMPORT
import android.view.ViewGroup // ADD THIS IMPORT
import androidx.lifecycle.viewmodel.compose.viewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // ... your existing viewModel and permission code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup the UI Content first (Compose)
        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    DashboardScreen(viewModel())
                }
            }
        }


    }
}