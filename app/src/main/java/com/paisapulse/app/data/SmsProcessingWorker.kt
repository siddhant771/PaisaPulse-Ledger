package com.paisapulse.app.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paisapulse.app.data.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SmsProcessingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: TransactionRepository
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val rawSms = inputData.getString(KEY_RAW_SMS) ?: return Result.failure()
        return try {
            if (repository.processSms(applicationContext, rawSms)) TransactionNotifier.showTransactionCaptured(applicationContext)
            Result.success()
        } catch (_: Exception) {
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val KEY_RAW_SMS = "raw_sms"
        private const val MAX_RETRIES = 4
    }
}
