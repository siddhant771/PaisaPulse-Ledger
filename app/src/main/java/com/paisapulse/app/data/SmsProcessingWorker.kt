package com.paisapulse.app.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paisapulse.app.data.repository.TransactionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SmsWorkerEntryPoint {
    fun transactionRepository(): TransactionRepository
}

class SmsProcessingWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    private val repository: TransactionRepository by lazy {
        EntryPointAccessors.fromApplication(applicationContext, SmsWorkerEntryPoint::class.java)
            .transactionRepository()
    }
    override suspend fun doWork(): Result {
        val rawSms = inputData.getString(KEY_RAW_SMS) ?: return Result.failure()
        return try {
            if (repository.processSms(applicationContext, rawSms)) {
                SmsStatusStore.set(applicationContext, "Last transaction categorized successfully")
                TransactionNotifier.showTransactionCaptured(applicationContext)
            } else {
                SmsStatusStore.set(applicationContext, "Transaction was ignored because it was already processed or not confidently recognized")
            }
            Result.success()
        } catch (_: Exception) {
            if (runAttemptCount < MAX_RETRIES) { SmsStatusStore.set(applicationContext, "Categorization will retry when service is available"); Result.retry() } else { SmsStatusStore.set(applicationContext, "Categorization failed. You can add this transaction manually."); Result.failure() }
        }
    }

    companion object {
        const val KEY_RAW_SMS = "raw_sms"
        private const val MAX_RETRIES = 4
    }
}
