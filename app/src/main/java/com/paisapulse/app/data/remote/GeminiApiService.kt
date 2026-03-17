package com.paisapulse.app.data.remote


import com.paisapulse.app.data.remote.models.GeminiRequest
import com.paisapulse.app.data.remote.models.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApiService {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun categorizeTransaction(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}