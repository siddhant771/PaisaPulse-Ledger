package com.paisapulse.app.data
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.repository.ITransactionRepository
import com.paisapulse.app.utils.SMSUtils

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint // This tells Hilt to allow injection here
class SmsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: ITransactionRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val body = message.messageBody
                val amount = SMSUtils.parseAmount(body)
                val category = SMSUtils.getCategory(body)

                if (amount != null) {
                    // Logic to extract merchant from sender address
                    val sender = message.displayOriginatingAddress ?: "Unknown"

                    val transaction = TransactionEntity(
                        amount = amount,
                        merchant = sender,
                        timestamp = System.currentTimeMillis(),
                        category = category,
                        rawBody = body
                    )

                    // Save to DB in a background thread
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.insertTransaction(transaction)
                    }
                }
            }
        }
    }
}