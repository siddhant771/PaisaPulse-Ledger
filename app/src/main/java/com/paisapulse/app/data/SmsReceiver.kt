package com.paisapulse.app.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paisapulse.app.data.repository.TransactionRepository
import com.paisapulse.app.data.utils.SmsParser

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        Telephony.Sms.Intents.getMessagesFromIntent(intent)
            .mapNotNull { it.displayMessageBody }
            .filter(SmsParser::isLikelyTransaction)
            .forEach { rawSms ->
                val request = OneTimeWorkRequestBuilder<SmsProcessingWorker>()
                    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .setInputData(Data.Builder().putString(SmsProcessingWorker.KEY_RAW_SMS, rawSms).build())
                    .build()
                WorkManager.getInstance(context).enqueueUniqueWork(
                    "sms-${TransactionRepository.fingerprint(rawSms)}",
                    ExistingWorkPolicy.KEEP,
                    request
                )
            }
    }
}
