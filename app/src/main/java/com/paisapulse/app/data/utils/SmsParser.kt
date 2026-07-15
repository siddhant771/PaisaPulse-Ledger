//package com.paisapulse.app.data.utils
//
//import java.util.regex.Pattern
//
//object SmsParser {
//    // Regex optimized for Indian banking formats (UPI, Netbanking, Cards)
//    private val AMOUNT_REGEX = Pattern.compile("(?i)(?:Rs\\.?|INR|Rs)\\s*([\\d,]+(?:\\.\\d{1,2})?)")
//    private val DEBIT_KEYWORDS = arrayOf("debited", "sent", "paid", "spent", "charged")
//
//    data class ParsedTransaction(
//        val amount: Double,
//        val merchant: String
//    )
//
//    fun parseIncomingSms(messageBody: String): ParsedTransaction? {
//        val lowercaseBody = messageBody.lowercase()
//
//        // Security Guardrail: Only process the text if it explicitly indicates an expense
//        val isDebit = DEBIT_KEYWORDS.any { lowercaseBody.contains(it) }
//        if (!isDebit) return null
//
//        val matcher = AMOUNT_REGEX.matcher(messageBody)
//        if (matcher.find()) {
//            val amountString = matcher.group(1)?.replace(",", "") ?: return null
//            val cleanAmount = amountString.toDoubleOrNull() ?: return null
//
//            // Fallback merchant identifier until Gemini AI analyzes it on Day 3
//            val merchant = extractBasicMerchant(messageBody)
//
//            return ParsedTransaction(amount = cleanAmount, merchant = merchant)
//        }
//        return null
//    }
//
//    private fun extractBasicMerchant(message: String): String {
//        return when {
//            message.contains("at", ignoreCase = true) -> {
//                message.substringAfter("at ").substringBefore(".").take(20).trim()
//            }
//            message.contains("to", ignoreCase = true) -> {
//                message.substringAfter("to ").substringBefore(".").take(20).trim()
//            }
//            else -> "Unknown Merchant"
//        }
//    }
//}