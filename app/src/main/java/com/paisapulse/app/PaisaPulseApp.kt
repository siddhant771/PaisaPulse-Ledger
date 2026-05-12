package com.paisapulse.app

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.paisapulse.app.data.worker.AiRetryWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class PaisaPulseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleAiRetryWorker()
    }

    private fun scheduleAiRetryWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<AiRetryWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ai_retry_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}