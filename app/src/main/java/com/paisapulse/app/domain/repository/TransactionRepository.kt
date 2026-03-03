package com.paisapulse.app.domain.repository

import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.models.CategorySum

import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transaction: TransactionEntity)
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    suspend fun deleteTransaction(transaction: TransactionEntity)
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>
    fun getTotalByCategory(category: String): Flow<Double?>
    fun getTotalExpenses(): Flow<Double?>
    fun getCategoryTotals(): Flow<List<CategorySum>>
}