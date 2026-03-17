package com.paisapulse.app.domain.usecase





import com.paisapulse.app.BuildConfig
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.data.remote.GeminiApiService
import com.paisapulse.app.data.remote.models.GeminiRequest
import com.paisapulse.app.data.remote.models.Content
import com.paisapulse.app.data.remote.models.Part
import com.paisapulse.app.data.sms.ParsedSms
import com.paisapulse.app.local.dao.TransactionDao
import javax.inject.Inject

class CategorizeTransactionUseCase @Inject constructor(
    private val api: GeminiApiService,
    private val dao: TransactionDao
) {
    suspend operator fun invoke(parsedSms: ParsedSms) {
        val prompt = "Analyze this SMS: '${parsedSms.rawBody}'. " +
                "Return ONLY one word for the category: Food, Shopping, Transport, Bills, Health, or Others."

        val category = try {
            val response = api.categorizeTransaction(
                apiKey = BuildConfig.GEMINI_API_KEY,
                request = GeminiRequest(listOf(Content(listOf(Part(prompt)))))
            )
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Others"
        } catch (e: Exception) {
            "Others"
        }

        dao.insertTransaction(
            TransactionEntity(
                amount = parsedSms.amount,
                merchant = parsedSms.merchant,
                category = category,
                timestamp = parsedSms.timestamp,
                rawBody = parsedSms.rawBody
            )
        )
    }
}