package com.paisapulse.app.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.paisapulse.app.data.local.TransactionEntity

@Composable
fun AddTransactionDialog(onDismiss: () -> Unit, onConfirm: (Double, String, String, Boolean) -> Unit, existing: TransactionEntity? = null) {
    var amount by remember { mutableStateOf(existing?.amount?.toString().orEmpty()) }
    var description by remember { mutableStateOf(existing?.description.orEmpty()) }
    var category by remember { mutableStateOf(existing?.category ?: "Food") }
    var isExpense by remember { mutableStateOf(existing?.isExpense ?: true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existing == null) "Add transaction" else "Edit transaction") },
        text = { Column {
            OutlinedTextField(description, { description = it }, label = { Text("Description") })
            OutlinedTextField(amount, { amount = it }, label = { Text("Amount") })
            OutlinedTextField(category, { category = it }, label = { Text("Category") })
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Expense"); Switch(isExpense, { isExpense = it }) }
        } },
        confirmButton = { Button(onClick = {
            val parsed = amount.toDoubleOrNull()
            if (parsed != null && parsed > 0 && description.isNotBlank()) onConfirm(parsed, description.trim(), category.trim(), isExpense)
        }) { Text("Save") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } }
    )
}
