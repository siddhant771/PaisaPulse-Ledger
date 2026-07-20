package com.paisapulse.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.paisapulse.app.security.BiometricPreferences
import com.paisapulse.app.ui.dashboard.DashboardScreen
import com.paisapulse.app.ui.onboarding.ConsentScreen
import com.paisapulse.app.ui.theme.PaisaPulseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private var onboardingComplete by mutableStateOf(false)
    private var biometricUnlocked by mutableStateOf(false)
    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { onboardingComplete = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onboardingComplete = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETE, false)
        biometricUnlocked = !BiometricPreferences.isEnabled(this)
        setContent {
            PaisaPulseTheme {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when {
                        !onboardingComplete -> ConsentScreen(
                            onContinue = { requestTrackingPermissions() },
                            onManualOnly = { completeOnboarding() }
                        )
                        !biometricUnlocked -> AppLockScreen(onUnlock = ::authenticate)
                        else -> DashboardScreen()
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

    private fun authenticate() {
        val prompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    biometricUnlocked = true
                }
            }
        )
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock PaisaPulse")
            .setSubtitle("Use your fingerprint, face unlock, or device screen lock")
            .setAllowedAuthenticators(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        prompt.authenticate(info)
    }

    override fun onStop() {
        super.onStop()
        // Lock again when the app leaves the foreground; no financial data remains visible in Recents.
        if (onboardingComplete && BiometricPreferences.isEnabled(this)) biometricUnlocked = false
    }

    private companion object {
        const val PREFERENCES = "paisapulse_preferences"
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}

@Composable
private fun AppLockScreen(onUnlock: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PaisaPulse", style = MaterialTheme.typography.displaySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold)
        Text(
            "Your financial data is protected.",
            modifier = Modifier.padding(top = 10.dp, bottom = 24.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onUnlock, modifier = Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text("Unlock securely") }
    }
}
