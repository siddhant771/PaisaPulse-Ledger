package com.paisapulse.app.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.data.remote.GeminiClient
import com.paisapulse.app.domain.repository.ITransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUserMessage: Boolean,
    val timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
)

@HiltViewModel
class AiCoachViewModel @Inject constructor(
    private val repository: ITransactionRepository
) : ViewModel() {

    private val _messages = MutableLiveData<List<ChatMessage>>()
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _suggestedQuestions = MutableLiveData<List<String>>()
    val suggestedQuestions: LiveData<List<String>> = _suggestedQuestions

    private val conversationContext = mutableListOf<ChatMessage>()

    init {
        initializeCoach()
    }

    private fun initializeCoach() {
        val greeting = ChatMessage(
            text = "Hello! I'm your AI financial coach. How can I help you today?",
            isUserMessage = false,
            timestamp = java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm")
            )
        )
        conversationContext.add(greeting)
        _messages.value = conversationContext.toList()

        updateSuggestedQuestions()
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.trim().isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true

            // Add user message to conversation
            val userMsg = ChatMessage(
                text = userMessage,
                isUserMessage = true
            )
            conversationContext.add(userMsg)
            _messages.value = conversationContext.toList()

            // Get real-time transaction data for context
            val transactions = repository.getAllTransactions()

            // Generate AI response
            val aiResponse = generateAiResponse(userMessage)
            val aiMsg = ChatMessage(
                text = aiResponse,
                isUserMessage = false
            )
            conversationContext.add(aiMsg)
            _messages.value = conversationContext.toList()

            // Update suggested questions based on context
            updateSuggestedQuestions()
            _isLoading.value = false
        }
    }

    private suspend fun generateAiResponse(userMessage: String): String {
        return try {
            // Build context from transaction data
            val totalExpensesFlow = repository.getTotalExpenses()
            var totalExpenses = 0.0

            totalExpensesFlow.collect { expenses ->
                totalExpenses = expenses ?: 0.0
            }

            val categoryTotalsFlow = repository.getCategoryTotals()
            val categoryInfo = buildString {
                categoryTotalsFlow.collect { categories ->
                    categories.forEach { category ->
                        append("${category.category}: ₹${category.total}, ")
                    }
                }
            }

            val prompt = """
                You are a helpful financial coach. The user's current financial status:
                - Total Expenses: ₹$totalExpenses
                - Category Breakdown: $categoryInfo
                
                User asked: "$userMessage"
                
                Provide a helpful, concise financial advice in 1-2 sentences. Be friendly and actionable.
            """.trimIndent()

            val response = GeminiClient.generateContent(prompt)
            response.ifBlank { "Keep tracking your expenses to achieve your financial goals!" }
        } catch (e: Exception) {
            "I'm having trouble processing your request. Please try again."
        }
    }

    private fun updateSuggestedQuestions() {
        val questions = listOf(
            "How much have I spent this week?",
            "Can you help me save money?",
            "What's my spending forecast?",
            "How am I doing with my budget?"
        )
        _suggestedQuestions.value = questions
    }

    fun handleSuggestedQuestion(question: String) {
        sendMessage(question)
    }
}
