package com.paisapulse.app.domain.repository

import com.paisapulse.app.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface ITransactionRepository {
    // We return a Flow so the UI stays "Live"
    fun getLiveTotalBalance(): Flow<Double?>

    // Suspend for background thread safety
    suspend fun insertTransaction(transaction: TransactionEntity)
}