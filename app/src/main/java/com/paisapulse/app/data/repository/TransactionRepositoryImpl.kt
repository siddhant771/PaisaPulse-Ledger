package com.paisapulse.app.data.repository


import com.paisapulse.app.data.local.entities.BudgetEntity
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.data.local.dao.TransactionDao
import com.paisapulse.app.data.remote.GeminiClient
import com.paisapulse.app.domain.models.BudgetStatus
import com.paisapulse.app.domain.models.CategorySum
import com.paisapulse.app.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : ITransactionRepository {
    private val defaultBudgets = listOf(
        BudgetEntity(category = "Food", monthlyLimit = 6000.0),
        BudgetEntity(category = "Travel", monthlyLimit = 4000.0),
        BudgetEntity(category = "Bills", monthlyLimit = 8000.0),
        BudgetEntity(category = "Shopping", monthlyLimit = 5000.0)
    )


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

    override fun getBudgetStatus(): Flow<List<BudgetStatus>> =
        transactionDao.getCategoryTotals().combine(transactionDao.getAllBudgets()) { totals, budgets ->
            val effectiveBudgets = if (budgets.isEmpty()) defaultBudgets else budgets
            effectiveBudgets.map { budget ->
                val spent = totals.find { it.category.equals(budget.category, ignoreCase = true) }?.total ?: 0.0
                val percent = if (budget.monthlyLimit > 0.0) {
                    ((spent / budget.monthlyLimit) * 100).toInt()
                } else {
                    0
                }

                BudgetStatus(
                    category = budget.category,
                    spentAmount = spent,
                    limitAmount = budget.monthlyLimit,
                    percentUsed = percent.coerceAtMost(100),
                    isOverThreshold = percent >= (budget.alertThreshold * 100)
                )
            }
        }

    override suspend fun upsertBudget(budget: BudgetEntity) {
        transactionDao.upsertBudget(budget)
    }

    override suspend fun getSpecificBudgetStatus(category: String): BudgetStatus? {
        val statuses = getBudgetStatus().first()
        return statuses.find { it.category.equals(category, ignoreCase = true) }
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    // 6. Delete a transaction (The swipe-to-delete logic)
    override suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }

    // 7. Add a new transaction
    override suspend fun insertTransaction(transaction: TransactionEntity) {
        seedBudgetsIfNeeded()

        val shouldCategorize = transaction.category.equals("Others", ignoreCase = true) ||
            transaction.category.equals("Uncategorized", ignoreCase = true)

        val aiCategory = if (shouldCategorize) {
            GeminiClient.categorizeTransaction(transaction.rawBody)
        } else {
            transaction.category
        }

        val updatedTransaction = transaction.copy(category = aiCategory)
        transactionDao.insertTransaction(updatedTransaction)
    }

    private suspend fun seedBudgetsIfNeeded() {
        if (transactionDao.getAllBudgets().first().isEmpty()) {
            defaultBudgets.forEach { budget -> transactionDao.upsertBudget(budget) }
        }
    }
}