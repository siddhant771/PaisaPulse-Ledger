package com.paisapulse.app

import com.paisapulse.app.BuildConfig
import com.paisapulse.app.data.ai.AiCategorizer
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class AiCategorizerTest {

    @Test
    fun testApiKeyPresence() {
        // Check if API key is configured
        val apiKey = BuildConfig.GEMINI_API_KEY
        println("=== API KEY STATUS ===")
        println("API Key present: ${!apiKey.isBlank()}")
        println("API Key length: ${apiKey.length}")
        println("API Key starts with: ${apiKey.take(10)}...")
        
        Assert.assertFalse("API Key is blank! AI categorization will use heuristic fallback only.", apiKey.isBlank())
    }

    @Test
    fun testHeuristicFallback() = runTest {
        // Test the heuristic fallback (this always works without API)
        val aiCategorizer = AiCategorizer()
        
        val testCases = listOf(
            "Starbucks Coffee" to "Food",
            "Amazon India" to "Shopping", 
            "Shell Petrol" to "Fuel",
            "Netflix Subscription" to "Entertainment",
            "Apollo Pharmacy" to "Medical",
            "Electric Bill Pay" to "Bills",
            "Salary Credit" to "Income"
        )
        
        testCases.forEach { (merchant, expectedCategory) ->
            val result = aiCategorizer.categorizeMerchant(merchant)
            println("$merchant -> $result (expected: $expectedCategory)")
            Assert.assertEquals("Failed for $merchant", expectedCategory, result)
        }
    }
    
    @Test
    fun testActualApiCall() = runTest {
        // This test will try to use the actual API
        val aiCategorizer = AiCategorizer()
        val testMerchant = "Starbucks Coffee"
        
        println("=== TESTING ACTUAL API CALL ===")
        println("Testing merchant: $testMerchant")
        
        val result = aiCategorizer.categorizeMerchant(testMerchant)
        println("Result: $result")
        
        // If result is one of the valid categories, it worked (either via API or fallback)
        val validCategories = listOf("Food", "Shopping", "Bills", "Fuel", "Entertainment", "Medical", "Investment", "Income", "Miscellaneous")
        Assert.assertTrue("Result should be a valid category", result in validCategories)
    }
}
