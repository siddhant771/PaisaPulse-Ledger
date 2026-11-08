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
interface WorkerEntryPoint {

    fun repository(): TransactionRepository
}

class Worker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository by lazy {

        EntryPointAccessors.fromApplication(
            applicationContext,
            WorkerEntryPoint::class.java
        ).repository()
    }

    override suspend fun doWork(): Result {

        val text = inputData.getString(KEY_TEXT)
            ?: return Result.failure()

        val source = inputData.getString(KEY_SOURCE)
            ?: return Result.failure()

        return try {

            repository.processCapturedTransaction(
                rawText = text,
                source = source
            )

            Result.success()

        } catch (exception: Exception) {

            if (runAttemptCount < 4) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {

        const val KEY_TEXT = "transaction_text"
        const val KEY_SOURCE = "transaction_source"
    }
}