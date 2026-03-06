package com.paisapulse.app.utils



object SMSUtils {
    // Regex updated to capture amounts with dots or commas
    private val amountRegex = Regex("(?i)(?:(?:RS|INR|USD|GBP|EUR|\\$|₹|€)\\.?\\s*)(\\d+(?:[.,]\\d+)*)|(\\d+(?:[.,]\\d+)*)\\s*(?:RS|INR|USD|GBP|EUR|\\$|₹|€)")

    fun parseAmount(message: String): Double {
        return try {
            val match = amountRegex.find(message)
            var amountStr = match?.groupValues?.get(1)?.takeIf { it.isNotEmpty() }
                ?: match?.groupValues?.get(2)

            if (amountStr != null) {
                // SDE-2 Normalization:
                // 1. If it has both (1,250.50), remove the comma (thousand separator)
                if (amountStr.contains(",") && amountStr.contains(".")) {
                    amountStr = amountStr.replace(",", "")
                }
                // 2. If it has ONLY a comma (25,50), treat it as a decimal point
                else if (amountStr.contains(",") && !amountStr.contains(".")) {
                    amountStr = amountStr.replace(",", ".")
                }
                amountStr.toDouble()
            } else 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    fun getCategory(message: String): String {
        val msg = message.lowercase()
        return when {
            listOf("zomato", "swiggy", "restaurant", "eat", "blinkit", "zepto").any { msg.contains(it) } -> "Food"
            listOf("uber", "ola", "rapido", "irctc", "petrol", "fuel").any { msg.contains(it) } -> "Travel"
            listOf("airtel", "jio", "recharge", "electricity", "bescom").any { msg.contains(it) } -> "Bills"
            else -> "Shopping"
        }
    }

    fun parseMerchant(message: String): String {
        val merchantRegex = Regex("(?i)(?:at|to|on|info)\\s+([A-Z0-9\\s*]+?)(?:on|using|at|ref|avl|\\.)")
        val match = merchantRegex.find(message)
        return match?.groupValues?.get(1)?.trim() ?: "Unknown Merchant"
    }
}