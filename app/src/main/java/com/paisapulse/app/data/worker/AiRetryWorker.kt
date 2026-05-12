package com.paisapulse.app.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.paisapulse.app.data.remote.GeminiClient
import com.paisapulse.app.domain.repository.ITransactionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

class AiRetryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = EntryPointAccessors
            .fromApplication(applicationContext, WorkerEntryPoint::class.java)
            .repository()

        val pending = repository.getTransactionsByCategory("Others").first()
        for (transaction in pending) {
            val aiCategory = GeminiClient.categorizeTransaction(transaction.rawBody)
            if (!aiCategory.equals("Others", ignoreCase = true)) {
                repository.updateTransaction(transaction.copy(category = aiCategory))
            }
        }
        return Result.success()
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerEntryPoint {
    fun repository(): ITransactionRepository
}
