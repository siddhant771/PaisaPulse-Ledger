package com.paisapulse.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["uniqueRemoteUuid"], unique = true)]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    val merchant: String,
    val timestamp: Long,
    val uniqueRemoteUuid: String,
    val isSynced: Boolean = false
)