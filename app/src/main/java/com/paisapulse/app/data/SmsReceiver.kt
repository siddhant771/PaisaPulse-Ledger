package com.paisapulse.app.data
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.BudgetDefaults
import com.paisapulse.app.domain.repository.ITransactionRepository
import com.paisapulse.app.utils.MerchantMapper
import com.paisapulse.app.utils.NotificationHelper
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
                    val sender = message.displayOriginatingAddress ?: "Unknown"
                    val cleanMerchant = MerchantMapper.cleanMerchantName(sender, body)

                    CoroutineScope(Dispatchers.IO).launch {
                        val totalSpent = repository.getAllTransactionsList().sumOf { it.amount }
                        val currentBalanceAfterThis =
                            BudgetDefaults.STARTING_BUDGET - (totalSpent + amount)

                        val transaction = TransactionEntity(
                            amount = amount,
                            merchant = cleanMerchant,
                            timestamp = System.currentTimeMillis(),
                            category = category,
                            rawBody = body,
                            currentBalance = currentBalanceAfterThis
                        )
                        repository.insertTransaction(transaction)

                        val status = repository.getSpecificBudgetStatus(category)
                        if (status?.isOverThreshold == true) {
                            NotificationHelper.showBudgetAlert(
                                context = context,
                                title = "Budget Alert!",
                                message = "You've used ${status.percentUsed}% of your ${status.category} budget."
                            )
                        }
                    }
                }
            }
        }
    }
}