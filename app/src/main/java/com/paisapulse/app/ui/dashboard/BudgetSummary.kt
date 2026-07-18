package com.paisapulse.app.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetSummary(spent: Double, budget: Double? = null, onSetBudget: () -> Unit = {}, onSetCategoryBudget: () -> Unit = {}) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Monthly budget", style = MaterialTheme.typography.titleMedium)
            if (budget == null) {
                Text("Set a monthly budget to track your spending.")
                Button(onClick = onSetBudget) { Text("Set budget") }
            } else {
                val remaining = budget - spent
                Text("₹${"%.0f".format(spent)} of ₹${"%.0f".format(budget)} spent")
                Text(if (remaining >= 0) "₹${"%.0f".format(remaining)} remaining" else "₹${"%.0f".format(-remaining)} over budget", color = if (remaining >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
            }
            Button(onClick = onSetCategoryBudget) { Text("Manage category limits") }
        }
    }
}
