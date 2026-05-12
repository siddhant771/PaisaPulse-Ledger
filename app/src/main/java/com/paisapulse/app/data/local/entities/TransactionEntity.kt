package com.paisapulse.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for every expense
    val amount: Double,        // The money value (e.g., 500.0)
    val merchant: String,      // Who you paid (e.g., Zomato)
    val timestamp: Long,       // The exact time of the SMS
    val category: String = "General",
    val rawBody: String,
    val currentBalance: Double = 0.0,
    val isRecurring: Boolean = false// New field to store the current balance after this transaction
)
