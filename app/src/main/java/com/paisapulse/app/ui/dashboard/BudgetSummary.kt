package com.paisapulse.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BudgetSummary(spent: Double, budget: Double?, onSetBudget: () -> Unit, onSetCategoryBudget: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Monthly budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                    Text("Keep your spending intentional", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = if (budget == null) onSetBudget else onSetBudget) { Text(if (budget == null) "Set budget" else "Edit") }
            }
            if (budget == null) {
                Text("Set one number to see how much is safe to spend this month.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                val fraction = (spent / budget).coerceIn(0.0, 1.0).toFloat()
                val remaining = budget - spent
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("₹${"%.0f".format(spent)} spent", fontWeight = FontWeight.Bold)
                    Text(if (remaining >= 0) "₹${"%.0f".format(remaining)} left" else "₹${"%.0f".format(-remaining)} over", color = if (remaining >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
                Box(Modifier.fillMaxWidth().height(9.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
                    Box(Modifier.fillMaxWidth(fraction).height(9.dp).clip(CircleShape).background(if (spent > budget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary))
                }
            }
            TextButton(onClick = onSetCategoryBudget, modifier = Modifier.padding(top = 2.dp)) { Text("Manage category limits") }
        }
    }
}
