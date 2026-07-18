package com.paisapulse.app.data.repository

import android.content.Context
import android.provider.Settings
import com.paisapulse.app.data.local.TransactionDao
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.data.remote.CategoryApi
import com.paisapulse.app.data.remote.SmsProcessRequest
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryApi: CategoryApi
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    suspend fun insertTransaction(transaction: TransactionEntity) = transactionDao.insertTransaction(transaction)

    suspend fun deleteTransaction(transactionId: Int) = transactionDao.deleteById(transactionId)

    suspend fun addManualTransaction(amount: Double, description: String, category: String, isExpense: Boolean, date: Long) {
        transactionDao.insertTransaction(TransactionEntity(amount = amount, description = description, category = category.ifBlank { "Other" }, isExpense = isExpense, date = date, source = "manual"))
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = transactionDao.updateTransaction(transaction)

    suspend fun processSms(context: Context, rawSms: String): Boolean {
        val response = categoryApi.processSms(SmsProcessRequest(rawSms, deviceId(context)))
        if (response.confidence_score <= 0.0 || response.amount <= 0.0) return false
        return try {
            transactionDao.insertTransaction(
                TransactionEntity(
                    amount = response.amount,
                    description = response.merchant,
                    category = response.category.ifBlank { "Uncategorized" },
                    isExpense = response.is_debit,
                    date = System.currentTimeMillis(),
                    source = "sms_ai",
                    smsFingerprint = fingerprint(rawSms),
                    aiConfidence = response.confidence_score
                )
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun deviceId(context: Context): String {
        val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        return MessageDigest.getInstance("SHA-256").digest(id.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    companion object {
        fun fingerprint(value: String): String = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
