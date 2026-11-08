package com.paisapulse.app.data.repository

import android.content.Context
import android.provider.Settings
import com.paisapulse.app.data.local.TransactionDao
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.data.remote.CategoryApi
import com.paisapulse.app.data.remote.TransactionRequest


import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val dao: TransactionDao,
    private val api: CategoryApi,
    @ApplicationContext private val context: Context
) {

    val allTransactions: Flow<List<TransactionEntity>> =
        dao.getAllTransactions()

    suspend fun addManualTransaction(
        amount: Double,
        description: String,
        category: String,
        isExpense: Boolean,
        date: Long
    ) {
        require(amount > 0 && amount.isFinite())

        dao.insertTransaction(
            TransactionEntity(
                amount = amount,
                description = description,
                category = category.ifBlank { "Other" },
                isExpense = isExpense,
                date = date,
                source = "manual"
            )
        )
    }

    suspend fun processCapturedTransaction(
        rawText: String,
        source: String
    ): Boolean {

        require(rawText.isNotBlank())

        val response = api.processTransaction(
            TransactionRequest(
                raw_sms = rawText,
                device_id = deviceId(),
                source = source
            )
        )

        if (response.amount <= 0 ||
            !response.amount.isFinite() ||
            response.confidence_score !in 0.80..1.0 ||
            response.merchant.isBlank() ||
            response.category.isBlank()
        ) {
            return false
        }

        val parsedDate = runCatching {
            LocalDate.parse(response.date)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrNull() ?: System.currentTimeMillis()

        val fingerprint = hash(
            listOf(
                response.amount.toBigDecimal().stripTrailingZeros().toPlainString(),
                response.merchant.trim().lowercase(),
                response.category.trim().lowercase(),
                response.is_debit.toString(),
                response.date
            ).joinToString("|")
        )

        val transaction = TransactionEntity(
            amount = response.amount,
            description = response.merchant,
            category = response.category,
            isExpense = response.is_debit,
            date = parsedDate,
            source = source,
            smsFingerprint = fingerprint,
            aiConfidence = response.confidence_score
        )

        return dao.insertTransaction(transaction) != -1L
    }

    suspend fun updateTransaction(
        transaction: TransactionEntity
    ) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Int) {
        dao.deleteById(id)
    }

    private fun deviceId(): String {

        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        return hash(androidId)
    }

    private fun hash(value: String): String {

        return MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") {
                "%02x".format(it)
            }
    }
}
