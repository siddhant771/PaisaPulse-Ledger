package com.paisapulse.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.paisapulse.app.ui.dashboard.DashboardScreen
import com.paisapulse.app.ui.onboarding.ConsentScreen
import com.paisapulse.app.ui.security.authenticateUser
import com.paisapulse.app.ui.theme.PaisaPulseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private var onboardingComplete by mutableStateOf(false)
    private var authenticated by mutableStateOf(false)

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { onboardingComplete = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onboardingComplete = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETE, false)
        setContent {
            PaisaPulseTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when {
                        !onboardingComplete -> ConsentScreen(
                            onContinue = { requestTrackingPermissions() },
                            onManualOnly = { completeOnboarding() }
                        )
                        authenticated -> DashboardScreen()
                        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Button(onClick = { authenticateUser(this@MainActivity) { authenticated = true } }) {
                                Text("Unlock PaisaPulse")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestTrackingPermissions() {
        val permissions = buildList {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.RECEIVE_SMS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (permissions.isEmpty()) completeOnboarding() else permissionsLauncher.launch(permissions.toTypedArray())
    }

    private fun completeOnboarding() {
        getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
        onboardingComplete = true
    }

    private companion object {
        const val PREFERENCES = "paisapulse_preferences"
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}
