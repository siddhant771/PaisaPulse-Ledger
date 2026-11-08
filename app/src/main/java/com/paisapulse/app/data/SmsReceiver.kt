package com.paisapulse.app.data


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.paisapulse.app.data.utils.SmsParser

private fun enqueueTransaction(
    context: Context,
    text: String,
    source: String
) {

    if (text.isBlank()) return

    val input = Data.Builder()
        .putString(Worker.KEY_TEXT, text)
        .putString(Worker.KEY_SOURCE, source)
        .build()

    val request =
        OneTimeWorkRequestBuilder<Worker>()
            .setInputData(input)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

    val id = java.security.MessageDigest
        .getInstance("SHA-256")
        .digest("$source|$text".toByteArray())
        .joinToString("") { "%02x".format(it) }

    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            "capture-$id",
            ExistingWorkPolicy.KEEP,
            request
        )
}

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val messages =
            Telephony.Sms.Intents.getMessagesFromIntent(intent)

        messages
            .mapNotNull { it.displayMessageBody }
            .filter { SmsParser.isLikelyTransaction(it) }
            .forEach {
                enqueueTransaction(
                    context,
                    it,
                    "sms"
                )
            }
    }
}

class PaisaPulseNotificationListener :
    NotificationListenerService() {

    private val allowedPackages = setOf(
        "com.phonepe.app",
        "com.google.android.apps.nbu.paisa.user",
        "net.one97.paytm",
        "com.application.zomato",
        "in.swiggy.android",
        "com.ubercab",
        "com.olacabs.customer"
    )

    override fun onNotificationPosted(
        notification: StatusBarNotification
    ) {

        if (notification.packageName !in allowedPackages) {
            return
        }

        val extras = notification.notification.extras

        val title = extras.getCharSequence("android.title")
            ?.toString().orEmpty()

        val text = extras.getCharSequence("android.text")
            ?.toString().orEmpty()

        val combined = "$title $text".trim()

        if (!isFinancialNotification(combined)) {
            return
        }

        enqueueTransaction(
            applicationContext,
            combined,
            "notification"
        )
    }

    private fun isFinancialNotification(text: String): Boolean {

        val normalized = text.lowercase()

        val financialTerms = listOf(
            "paid",
            "debited",
            "spent",
            "payment successful",
            "transaction successful",
            "sent",
            "received"
        )

        val otpTerms = listOf(
            "otp",
            "verification code",
            "one time password"
        )

        return financialTerms.any {
            normalized.contains(it)
        } && otpTerms.none {
            normalized.contains(it)
        }
    }
}