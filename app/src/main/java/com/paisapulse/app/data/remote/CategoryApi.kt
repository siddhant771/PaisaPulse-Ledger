package com.paisapulse.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST


data class CategorizationRequest(val description: String)
data class CategorizationResponse(val category: String)
interface CategoryApi {
    @POST("/categorize")
    suspend fun getCategory(@Body request:CategorizationRequest): CategorizationResponse
}

