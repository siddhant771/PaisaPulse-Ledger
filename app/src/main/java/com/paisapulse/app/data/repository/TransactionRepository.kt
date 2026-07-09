package com.paisapulse.app.data.repository

import android.accessibilityservice.GestureDescription
import android.util.Log
import androidx.room.Transaction
import com.paisapulse.app.data.local.TransactionDao
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.data.remote.CategorizationRequest
import com.paisapulse.app.data.remote.CategoryApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryApi: CategoryApi
) {
    val allTransactions : Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    suspend fun insertTransaction(transaction: TransactionEntity){
        transactionDao.insertTransaction(transaction)
    }
    suspend fun insertTransactionWithCategory(amount:Double,description:String,isExpense:Boolean,date:Long = System.currentTimeMillis()){
        val category = try {
            val response = categoryApi.getCategory(CategorizationRequest(description))
            response.category
        }catch (e:Exception){
            Log.e("TransactionRepository", "Ai categorization failed: ${e.message}")
            "Uncategorized"
        }
        val transaction = TransactionEntity(amount = amount, category = category, isExpense = isExpense,date=System.currentTimeMillis())
        transactionDao.insertTransaction(transaction)

    }
}