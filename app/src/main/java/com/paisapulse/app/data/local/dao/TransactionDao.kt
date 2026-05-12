package com.paisapulse.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.paisapulse.app.data.local.entities.BudgetEntity
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.models.CategorySum
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    @Query("UPDATE transactions SET currentBalance = currentBalance - :amount")
    suspend fun subtractFromBalance(amount: Double)
    @Query("SELECT SUM(amount) FROM transactions")
    fun getTotalExpenses(): Flow<Double?>
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY timestamp DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE category = :category")
    fun getTotalByCategory(category: String): Flow<Double?>
    @Query("SELECT category, SUM(amount) as total FROM transactions GROUP BY category")
    fun getCategoryTotals(): Flow<List<CategorySum>>

    @Query("SELECT * FROM budgets")
    fun getAllBudgets(): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE LOWER(category) = LOWER(:category) LIMIT 1")
    suspend fun getBudgetByCategory(category: String): BudgetEntity?

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactionsList(): List<TransactionEntity>
    @Transaction
    suspend fun addExpenseWithUpdate(transaction: TransactionEntity) {
        insertTransaction(transaction)
        subtractFromBalance(transaction.amount)
    }
}