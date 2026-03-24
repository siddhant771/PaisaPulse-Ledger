package com.paisapulse.app.data.repository


import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.data.local.dao.TransactionDao
import com.paisapulse.app.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : ITransactionRepository {
    override fun getLiveTotalBalance(): Flow<Double?> = transactionDao.getTotalExpenses()

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }
}