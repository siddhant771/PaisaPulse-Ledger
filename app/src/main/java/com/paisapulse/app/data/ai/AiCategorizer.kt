package com.paisapulse.app.data.ai

import com.google.ai.client.generativeai.BuildConfig


import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiCategorizer @Inject constructor() {

    private val config = generationConfig {
        temperature = 0.1f
        maxOutputTokens = 15
    }

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun categorizeMerchant(merchantText: String): String =
        withContext(Dispatchers.IO) {
            try {
                val response = model.generateContent(
                    "Categorize this merchant transaction: $merchantText"
                )

                val cleanResult = response.text?.trim() ?: "Miscellaneous"

                val validCategories = listOf(
                    "Food", "Shopping", "Bills", "Fuel",
                    "Entertainment", "Medical", "Investment", "Income"
                )

                if (validCategories.contains(cleanResult)) {
                    cleanResult
                } else {
                    "Miscellaneous"
                }

            } catch (e: Exception) {
                e.printStackTrace()
                "Miscellaneous"
            }
        }
}