package com.paisapulse.app.utils

import android.util.Log

object SMSUtils {

    private const val TAG = "SMSUtils"

    // Amount regex (robust)
    private val amountRegex =
        Regex("(?i)(?:Rs\\.?|INR|₹|USD|\\$|€|amt)\\s*([\\d,.]+\\d*)")

    // Merchant regex
    private val merchantRegex =
        Regex("(?i)(?:at|to|vpa|on|info)\\s+([A-Za-z0-9*\\s]{2,20}?)(\\s+using|\\s+on|\\s+for|\\.|$)")

    // Filters
    private val exclusionKeywords =
        listOf("otp", "login", "credited", "received", "deposited", "refund")

    private val debitKeywords =
        listOf("debited", "spent", "paid", "transaction", "withdrawal")

    // 🔹 Parse Amount
    fun parseAmount(message: String): Double? {
        return try {
            val match = amountRegex.find(message)
            var amountStr = match?.groupValues?.get(1) ?: return null

            // Normalize
            if (amountStr.contains(",") && amountStr.contains(".")) {
                amountStr = amountStr.replace(",", "")
            } else if (amountStr.contains(",") && !amountStr.contains(".")) {
                amountStr = amountStr.replace(",", ".")
            }

            amountStr.toDoubleOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Amount parsing failed", e)
            null
        }
    }

    // 🔹 Parse Merchant
    fun parseMerchant(message: String): String {
        val match = merchantRegex.find(message)
        var merchant = match?.groupValues?.get(1)?.trim() ?: "Unknown"

        return merchant.replace("*", "").trim()
    }

    // 🔹 Category
    fun getCategory(message: String): String {
        val msg = message.lowercase()

        return when {
            listOf("zomato", "swiggy", "restaurant", "eat", "blinkit", "zepto", "kfc").any {
                msg.contains(it)
            } -> "Food"

            listOf("uber", "ola", "rapido", "irctc", "petrol", "fuel").any {
                msg.contains(it)
            } -> "Travel"

            listOf("airtel", "jio", "recharge", "electricity", "bill").any {
                msg.contains(it)
            } -> "Bills"

            else -> "Shopping"
        }
    }

    // 🔥 Main filter (IMPORTANT)
    fun isValidExpense(message: String): Boolean {
        val msg = message.lowercase()

        if (exclusionKeywords.any { msg.contains(it) }) return false
        if (!debitKeywords.any { msg.contains(it) }) return false

        return true
    }
}