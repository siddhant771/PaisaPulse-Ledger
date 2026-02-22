package com.paisapulse.app.utils

object SmsParser {
    // This pattern looks for Rs. or ₹ followed by digits/decimals
    private val amountRegex = Regex("(?:Rs\\.?|INR|₹)\\s?([\\d,]+\\.?\\d{2})")

    fun parseAmount(message: String): Double? {
        val match = amountRegex.find(message)
        return match?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
    }
}