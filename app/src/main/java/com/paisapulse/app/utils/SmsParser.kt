package com.paisapulse.app.utils

object SmsParser {
    private val amountRegex = Regex("(?i)(?:Rs\\.?|INR|₹)\\s?([\\d,]+(?:\\.\\d{1,2})?)")

    // Intelligent Keywords for categorization
    private val foodKeywords = listOf("zomato", "swiggy", "restaurant", "eat", "blinkit", "zepto")
    private val travelKeywords = listOf("uber", "ola", "rapido", "irctc", "petrol", "fuel")
    private val billsKeywords = listOf("airtel", "jio", "recharge", "electricity", "bescom")

    fun parseAmount(message: String): Double? {
        val match = amountRegex.find(message)
        return match?.groupValues?.get(1)?.replace(",", "")?.toDoubleOrNull()
    }

    // New Function: Detect Category
    fun getCategory(message: String): String {
        val msg = message.lowercase()
        return when {
            foodKeywords.any { msg.contains(it) } -> "Food"
            travelKeywords.any { msg.contains(it) } -> "Travel"
            billsKeywords.any { msg.contains(it) } -> "Bills"
            else -> "Shopping"
        }
    }
}