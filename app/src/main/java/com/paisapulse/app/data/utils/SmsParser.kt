package com.paisapulse.app.data.utils



object SmsParser {

    private val transactionKeywords = listOf(
        "debited",
        "credited",
        "spent",
        "paid",
        "payment",
        "received",
        "withdrawn",
        "transferred"
    )

    private val amountRegex =
        Regex("""(?i)(rs\.?|inr|₹)\s*[0-9]""")

    private val excludedKeywords = listOf(
        "otp",
        "verification code",
        "one time password",
        "transaction failed",
        "payment failed"
    )

    fun isLikelyTransaction(message: String): Boolean {
        val normalized = message.lowercase()

        if (excludedKeywords.any { normalized.contains(it) }) {
            return false
        }

        val hasTransactionKeyword =
            transactionKeywords.any { normalized.contains(it) }

        val hasAmount = amountRegex.containsMatchIn(message)

        return hasTransactionKeyword && hasAmount
    }
}
