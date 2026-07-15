//package com.paisapulse.app.data
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.provider.Telephony
//import android.util.Log
//import com.paisapulse.app.data.local.TransactionDao
//import com.paisapulse.app.data.local.TransactionEntity
//import com.paisapulse.app.data.utils.SmsParser
//import com.paisapulse.app.data.ai.AiCategorizer
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//import java.util.UUID
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class SmsReceiver : BroadcastReceiver() {
//
//    @Inject
//    lateinit var transactionDao: TransactionDao
//
//    @Inject
//    lateinit var aiCategorizer: AiCategorizer // Day 3 AI Injection Hook
//
//    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
//            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
//            for (sms in messages) {
//                val body = sms.displayMessageBody ?: continue
//                val parsedResult = SmsParser.parseIncomingSms(body)
//
//                if (parsedResult != null) {
//                    receiverScope.launch {
//                        try {
//                            // AI-only categorization - no fallback
//                            val aiDetectedCategory = aiCategorizer.categorizeMerchant(parsedResult.merchant)
//
//                            val newTransaction = TransactionEntity(
//                                amount = parsedResult.amount,
//                                category = aiDetectedCategory,
//                                merchant = parsedResult.merchant,
//                                timestamp = System.currentTimeMillis(),
//                                uniqueRemoteUuid = UUID.randomUUID().toString(),
//                                isSynced = false
//                            )
//
//                            transactionDao.insertTransaction(newTransaction)
//                            Log.d("PaisaPulseFlow", "✅ AI Classification Success! Saved: ₹${newTransaction.amount} categorized as [$aiDetectedCategory] at ${newTransaction.merchant}")
//                        } catch (e: Exception) {
//                            Log.e("PaisaPulseFlow", "❌ Failed to categorize transaction '${parsedResult.merchant}': ${e.localizedMessage}. Transaction not saved.")
//                            // Transaction is not saved if AI fails - user wants AI-only categorization
//                        }
//                    }
//                }
//            }
//        }
//    }
//}