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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

// Change this import
import androidx.fragment.app.FragmentActivity
import com.paisapulse.app.ui.security.authenticateUser
import com.paisapulse.app.ui.theme.PaisaPulseTheme

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PaisaPulseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color=MaterialTheme.colorScheme.background
                ){
                    var isAuthenticated by remember { mutableStateOf(false) }
                    if (isAuthenticated) {
                        DashboardScreen()
                    } else {
                        // Simple "Unlock" Screen
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Button(onClick = {
                                authenticateUser(this@MainActivity) { isAuthenticated = true }
                            }) {
                                Text("Unlock PaisaPulse")
                            }
                        }
                    }

                }
            }



        }
    }
}