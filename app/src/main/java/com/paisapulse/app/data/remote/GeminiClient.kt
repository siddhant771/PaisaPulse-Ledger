package com.paisapulse.app.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.paisapulse.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiClient {
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun categorizeTransaction(smsBody: String): String = withContext(Dispatchers.IO) {
        val prompt = """
            Analyze this bank SMS: "$smsBody"
            Identify the merchant and categorize it into one of these:
            Food, Travel, Bills, Shopping, Health, Entertainment, or Others.
            Return ONLY the category name.
        """.trimIndent()

        return@withContext generateContent(prompt)
            .ifBlank { "Others" }
    }

    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return@withContext "Others"
        }

        return@withContext try {
            model.generateContent(prompt).text?.trim().orEmpty()
        } catch (_: Exception) {
            "Others"
        }
    }
}
