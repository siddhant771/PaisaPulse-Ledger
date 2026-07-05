import com.paisapulse.app.BuildConfig

fun main() {
    val apiKey = BuildConfig.GEMINI_API_KEY
    println("=== API KEY STATUS ===")
    println("API Key present: ${!apiKey.isBlank()}")
    println("API Key length: ${apiKey.length}")
    println("API Key starts with: ${apiKey.take(10)}...")
    
    if (apiKey.isBlank()) {
        println("❌ API KEY IS MISSING - AI categorization will use heuristic fallback only")
    } else {
        println("✅ API KEY IS PRESENT - AI categorization should work with Gemini API")
    }
}
