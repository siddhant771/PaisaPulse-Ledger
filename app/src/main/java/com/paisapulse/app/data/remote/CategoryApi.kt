package com.paisapulse.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class TransactionRequest(
    val raw_sms: String,
    val device_id: String,
    val source: String = "sms"
)

data class TransactionResponse(
    val amount: Double,
    val merchant: String,
    val category: String,
    val is_debit: Boolean,
    val date: String,
    val confidence_score: Double
)

interface CategoryApi {

    @POST("api/v1/process")
    suspend fun processTransaction(
        @Body request: TransactionRequest
    ): TransactionResponse
}