package com.paisapulse.app.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.models.CategorySum
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    @Query("SELECT SUM(amount) FROM transactions")
    fun getTotalExpenses(): Flow<Double?>
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY timestamp DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE category = :category")
    fun getTotalByCategory(category: String): Flow<Double?>
    @Query("SELECT category, SUM(amount) as total FROM transactions GROUP BY category")
    fun getCategoryTotals(): Flow<List<CategorySum>>
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactionsList(): List<TransactionEntity>
}