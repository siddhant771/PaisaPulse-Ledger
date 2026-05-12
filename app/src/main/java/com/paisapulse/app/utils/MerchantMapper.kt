package com.paisapulse.app.utils

object MerchantMapper {

    /**
     * Cleans bank-style merchant names into human-readable brand names.
     */
    fun cleanMerchantName(sender: String, body: String): String {
        val msg = body.lowercase()
        return when {
            msg.contains("zomato") -> "Zomato"
            msg.contains("swiggy") -> "Swiggy"
            msg.contains("uber") -> "Uber"
            msg.contains("ola") -> "Ola"
            msg.contains("amazon") -> "Amazon"
            msg.contains("flipkart") -> "Flipkart"
            msg.contains("blinkit") -> "Blinkit"
            msg.contains("recharge") || msg.contains("jio") -> "Mobile Recharge"
            else -> {
                sender.replace(Regex("^[A-Z]{2}-"), "").trim()
            }
        }
    }
}
