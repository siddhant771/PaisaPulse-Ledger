package com.paisapulse.app.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConsentScreen(onContinue: () -> Unit, onManualOnly: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(28.dp), verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Text("₹", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.ExtraBold)
        }
        Text("PaisaPulse", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 22.dp))
        Text("Your money, made clear.", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        Card(Modifier.fillMaxWidth().padding(top = 28.dp), shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OnboardingPoint(Icons.Default.Edit, "Automatic tracking", "PaisaPulse looks only for banking-style transaction SMS messages.")
                OnboardingPoint(Icons.Default.Lock, "You stay in control", "Continue manually at any time. Edit or delete every saved transaction.")
            }
        }
        Text("When enabled, qualifying messages are securely sent to the categorization service to identify the merchant, amount, and category. You can manage this permission later in Settings.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 20.dp, bottom = 20.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Enable SMS transaction tracking") }
        OutlinedButton(onClick = onManualOnly, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("Continue with manual tracking") }
    }
}

@Composable
private fun OnboardingPoint(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String) {
    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.padding(start = 12.dp)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
