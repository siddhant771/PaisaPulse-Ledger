package com.paisapulse.app.data.repository


import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.data.local.dao.TransactionDao
import com.paisapulse.app.domain.models.CategorySum
import com.paisapulse.app.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : ITransactionRepository {


    // 1. Get all transactions for the main list
    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    override suspend fun getAllTransactionsList(): List<TransactionEntity> {
        return transactionDao.getAllTransactionsList()
    }

    // 2. Filter by Category (Food, Travel, etc.)
    override fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByCategory(category)
    }

    // 3. Get total spent for a specific category
    override fun getTotalByCategory(category: String): Flow<Double?> {
        return transactionDao.getTotalByCategory(category)
    }

    // 4. Get the grand total of all expenses
    override fun getTotalExpenses(): Flow<Double?> {
        return transactionDao.getTotalExpenses()
    }

    // 5. Get data for your Pie Chart (Figma Design)
    override fun getCategoryTotals(): Flow<List<CategorySum>> {
        return transactionDao.getCategoryTotals()
    }

    // 6. Delete a transaction (The swipe-to-delete logic)
    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    // 7. Add a new transaction
    override suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }
}