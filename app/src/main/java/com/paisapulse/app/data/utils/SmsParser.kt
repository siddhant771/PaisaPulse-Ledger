package com.paisapulse.app.data.utils

object SmsParser {
    private val transactionKeywords = listOf("debited", "credited", "spent", "paid", "received", "withdrawn")

    /** Filters obvious non-financial messages before sending SMS text to the API. */
    fun isLikelyTransaction(message: String): Boolean {
        val normalized = message.lowercase()
        return transactionKeywords.any(normalized::contains) &&
            Regex("(?i)(rs\\.?|inr|₹)\\s*[0-9]").containsMatchIn(message)
    }
}
