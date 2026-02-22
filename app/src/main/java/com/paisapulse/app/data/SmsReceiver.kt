package com.paisapulse.app.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.paisapulse.app.utils.SmsParser

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val body = message.messageBody
                val amount = SmsParser.parseAmount(body)

                if (amount != null) {

                    android.widget.Toast.makeText(context, "Money detected: ₹$amount", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}