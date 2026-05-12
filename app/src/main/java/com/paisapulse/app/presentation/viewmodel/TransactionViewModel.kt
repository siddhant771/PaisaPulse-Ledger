package com.paisapulse.app.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.data.remote.GeminiClient
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.models.CategorySum
import com.paisapulse.app.domain.models.SmartInsight
import com.paisapulse.app.domain.repository.ITransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: ITransactionRepository
) : ViewModel() {

    val allTransactions: LiveData<List<TransactionEntity>> =
        repository.getAllTransactions().asLiveData()

    val totalExpenses: LiveData<Double?> =
        repository.getTotalExpenses().asLiveData()

    val categoryTotals: LiveData<List<CategorySum>> =
        repository.getCategoryTotals().asLiveData()

    val budgetStatuses = repository.getBudgetStatus().asLiveData()

    private val _aiInsight = MutableStateFlow<SmartInsight?>(null)
    val aiInsight: StateFlow<SmartInsight?> = _aiInsight.asStateFlow()
    private var lastInsightSeed: String? = null

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun insertTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    fun generateInsights(transactions: List<TransactionEntity>) {
        if (transactions.isEmpty()) return

        viewModelScope.launch {
            val summary = transactions.take(10).joinToString { "${it.merchant}: ${it.amount}" }
            if (summary == lastInsightSeed) return@launch
            lastInsightSeed = summary
            val prompt = "Based on these transactions: $summary. Give a 1-sentence financial tip."
            val tip = GeminiClient.generateContent(prompt).ifBlank { "Track recurring spends to improve savings." }
            _aiInsight.value = SmartInsight(
                title = "Smart Tip",
                description = tip,
                iconType = "AI"
            )
        }
    }
}
