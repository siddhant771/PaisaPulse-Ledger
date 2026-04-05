package com.paisapulse.app.domain.repository

import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.models.CategorySum
import kotlinx.coroutines.flow.Flow

interface ITransactionRepository {
    // We return a Flow so the UI stays "Live"
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    suspend fun getAllTransactionsList(): List<TransactionEntity>

    fun getTotalExpenses(): Flow<Double?> // This will be our "Live Balance" source
    suspend fun deleteTransaction(transaction: TransactionEntity)
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>
    fun getTotalByCategory(category: String): Flow<Double?>
    fun getCategoryTotals(): Flow<List<CategorySum>>
    suspend fun insertTransaction(transaction: TransactionEntity)
}