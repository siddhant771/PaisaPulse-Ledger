package com.paisapulse.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.data.repository.ExportRepository
import com.paisapulse.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _currentMonth = MutableStateFlow(LocalDate.now().monthValue)
    val currentMonth: StateFlow<Int> = _currentMonth.asStateFlow()

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(transactions, _currentMonth) { list, month ->
        list.filter { Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).monthValue == month }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val categoryTotals: StateFlow<Map<String, Double>> = transactions.map { list ->
        list.filter { it.isExpense }.groupBy { it.category }.mapValues { (_, entries) -> entries.sumOf { it.amount } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    init { _isLoading.value = false }

    fun setMonth(month: Int) { _currentMonth.value = month }

    fun addTransaction(amount: Double, description: String, category: String, isExpense: Boolean, date: Long) {
        viewModelScope.launch { repository.addManualTransaction(amount, description, category, isExpense, date) }
    }

    fun exportTransactions(onResult: (String) -> Unit) {
        viewModelScope.launch { onResult(ExportRepository().generateCsv(repository.allTransactions.first())) }
    }

    fun deleteTransaction(transactionId: Int) { viewModelScope.launch { repository.deleteTransaction(transactionId) } }

    fun updateTransaction(transaction: TransactionEntity) { viewModelScope.launch { repository.updateTransaction(transaction) } }
}
