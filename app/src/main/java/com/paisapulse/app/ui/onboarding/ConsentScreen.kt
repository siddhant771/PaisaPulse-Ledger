package com.paisapulse.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConsentScreen(onContinue: () -> Unit, onManualOnly: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to PaisaPulse", style = MaterialTheme.typography.headlineMedium)
        Text("Your private finance companion", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
        Text(
            "With your permission, PaisaPulse reads only transaction-like banking SMS messages. " +
                "It sends qualifying messages to our AI service to extract the amount, merchant, and category. " +
                "You can continue without SMS access and add transactions manually.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)
        )
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Allow SMS transaction tracking") }
        OutlinedButton(onClick = onManualOnly, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Continue with manual tracking") }
    }
}
