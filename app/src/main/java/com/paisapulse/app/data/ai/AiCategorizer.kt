//package com.paisapulse.app.data.ai
//
//import android.util.Log
//import com.google.ai.client.generativeai.GenerativeModel
//import com.google.ai.client.generativeai.type.content
//import com.google.ai.client.generativeai.type.generationConfig
//import com.paisapulse.app.BuildConfig
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class AiCategorizer @Inject constructor() {
//
//    private val validCategories = listOf(
//        "Food", "Shopping", "Bills", "Fuel",
//        "Entertainment", "Medical", "Investment", "Income", "Miscellaneous"
//    )
//
//    private val config = generationConfig {
//        temperature = 0.0f
//        maxOutputTokens = 10
//    }
//
//    private val model = GenerativeModel(
//        modelName = "gemini-3.5-flash",
//        apiKey = BuildConfig.GEMINI_API_KEY,
//        generationConfig = config,
//        systemInstruction = content("system") {
//            text(
//                "You are an expert financial ledger assistant. Your job is to read an unformatted merchant name or SMS string from a bank transaction and output EXACTLY one word representing the budget category. Choose ONLY from: Food, Shopping, Bills, Fuel, Entertainment, Medical, Investment, Income, or Miscellaneous. Do not write full sentences. Do not include punctuation, quotes, or any explanation. If uncertain, output Miscellaneous. Output just the single category word. For example, Starbucks Coffee -> Food. Amazon -> Shopping. Salary -> Income."
//            )
//        }
//    )
//
//    suspend fun categorizeMerchant(merchantText: String): String = withContext(Dispatchers.IO) {
//        // AI-only categorization - no fallback
//        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
//            Log.e("PaisaPulseFlow", "❌ Gemini Key missing! AI categorization requires API key.")
//            throw Exception("API key missing - cannot categorize without AI")
//        }
//
//        try {
//            val response = model.generateContent("Categorize this merchant transaction: $merchantText")
//            val rawResult = response.text?.trim() ?: ""
//            val normalizedResult = rawResult
//                .split(Regex("\\s+"))[0]
//                .replace(Regex("[^\\p{L}]"), "")
//                .replaceFirstChar { it.uppercase() }
//
//            if (normalizedResult in validCategories) {
//                Log.d("PaisaPulseFlow", "🤖 AI SUCCESS: Categorized '$merchantText' as [$normalizedResult]")
//                return@withContext normalizedResult
//            } else {
//                Log.e("PaisaPulseFlow", "❌ AI returned invalid category '$normalizedResult' for '$merchantText'")
//                throw Exception("AI returned invalid category: $normalizedResult")
//            }
//        } catch (e: Exception) {
//            Log.e("PaisaPulseFlow", "❌ AI ENGINE FAILED for '$merchantText': ${e.localizedMessage}")
//            throw e // Re-throw to let caller handle AI failure
//        }
//    }
//}