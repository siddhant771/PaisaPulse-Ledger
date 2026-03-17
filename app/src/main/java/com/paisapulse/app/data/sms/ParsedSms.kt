package com.paisapulse.app.data.sms

data class ParsedSms(
    val amount: Double,
    val merchant: String,
    val timestamp: Long,
    val rawBody: String
)