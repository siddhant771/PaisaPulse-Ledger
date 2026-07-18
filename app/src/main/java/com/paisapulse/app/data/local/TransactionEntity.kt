package com.paisapulse.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["smsFingerprint"], unique = true)]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Long,
    val isExpense: Boolean,
    val source: String = "manual",
    val smsFingerprint: String? = null,
    val aiConfidence: Double? = null
)
