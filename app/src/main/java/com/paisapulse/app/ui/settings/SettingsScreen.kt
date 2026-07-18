package com.paisapulse.app.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val smsGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("Settings & privacy", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("SMS transaction tracking", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(if (smsGranted) "SMS access is enabled." else "SMS access is off. You can still add transactions manually.")
                TextButton(onClick = {
                    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                }) { Text("Open app permissions") }
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("How your data is used", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("Only transaction-like SMS messages are sent to the categorization service. Transaction records remain on this device unless you export them.")
                Text("For account deletion or a production privacy policy, connect this screen to your published support and privacy-policy URLs before release.")
            }
        }
    }
}
