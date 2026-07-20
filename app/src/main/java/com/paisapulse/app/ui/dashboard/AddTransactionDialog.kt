package com.paisapulse.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paisapulse.app.data.local.TransactionEntity

@Composable
fun AddTransactionDialog(onDismiss: () -> Unit, onConfirm: (Double, String, String, Boolean) -> Unit, existing: TransactionEntity? = null) {
    var amount by remember { mutableStateOf(existing?.amount?.toString().orEmpty()) }
    var description by remember { mutableStateOf(existing?.description.orEmpty()) }
    var category by remember { mutableStateOf(existing?.category ?: "Food") }
    var isExpense by remember { mutableStateOf(existing?.isExpense ?: true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add transaction" else "Edit transaction", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Keep your ledger accurate. You can change AI-categorized entries anytime.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(description, { description = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Merchant or description") }, singleLine = true)
                OutlinedTextField(amount, { amount = it.filter { char -> char.isDigit() || char == '.' } }, modifier = Modifier.fillMaxWidth(), label = { Text("Amount (₹)") }, singleLine = true)
                OutlinedTextField(category, { category = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Category") }, singleLine = true)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text(if (isExpense) "Expense" else "Income", fontWeight = FontWeight.Bold)
                        Text("Toggle if this money came in", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = isExpense, onCheckedChange = { isExpense = it })
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                amount.toDoubleOrNull()?.takeIf { it > 0 }?.let { parsed ->
                    if (description.isNotBlank()) onConfirm(parsed, description.trim(), category.trim().ifBlank { "Other" }, isExpense)
                }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
