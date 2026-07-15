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

@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount:Double ,description:String,isExpense:Boolean) -> Unit
){
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Transaction")},
        text = {
            Column{
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
                OutlinedTextField(
                    value=amount,
                    onValueChange = {amount=it},
                    label = {Text("Amount")}


                )
                Row(
                    verticalAlignment= Alignment.CenterVertically
                ){
                    Text("Expense")
                    Switch(
                        checked=isExpense,
                        onCheckedChange = {isExpense=it}
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(amount.toDoubleOrNull() ?: 0.0, description, isExpense)
                onDismiss()
            }){
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss){
                Text("Cancel")
            }
        }


    )

}



