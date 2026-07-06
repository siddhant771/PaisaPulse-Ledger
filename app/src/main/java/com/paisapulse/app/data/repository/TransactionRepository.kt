package com.paisapulse.app.data.repository

import androidx.room.Transaction
import com.paisapulse.app.data.local.TransactionDao
import com.paisapulse.app.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    val allTransactions : Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    suspend fun insertTransaction(transaction: TransactionEntity){
        transactionDao.insertTransaction(transaction)
    }
}