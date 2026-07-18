package com.paisapulse.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class SmsProcessRequest(val raw_sms: String, val device_id: String)

data class StructuredTransactionResponse(
    val amount: Double,
    val merchant: String,
    val category: String,
    val is_debit: Boolean,
    val date: String,
    val confidence_score: Double
)

interface CategoryApi {
    @POST("api/v1/process")
    suspend fun processSms(@Body request: SmsProcessRequest): StructuredTransactionResponse
}
