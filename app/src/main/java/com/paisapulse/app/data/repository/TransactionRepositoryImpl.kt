package com.paisapulse.app.data.repository


import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.repository.TransactionRepository
import com.paisapulse.app.local.dao.TransactionDao
import com.paisapulse.app.domain.models.CategorySum
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override suspend fun saveTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }
    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }
    override fun getTotalExpenses(): Flow<Double?> = transactionDao.getTotalExpenses()
    override fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategory(category)
    }

    override fun getTotalByCategory(category: String): Flow<Double?> {
        return transactionDao.getTotalByCategory(category)
    }
    override fun getCategoryTotals(): Flow<List<CategorySum>> = transactionDao.getCategoryTotals()

}