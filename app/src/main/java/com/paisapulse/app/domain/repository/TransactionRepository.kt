package com.paisapulse.app.domain.repository

import com.paisapulse.app.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transaction: TransactionEntity)
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    suspend fun deleteTransaction(transaction: TransactionEntity)
}