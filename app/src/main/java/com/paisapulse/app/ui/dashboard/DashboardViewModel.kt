package com.paisapulse.app.ui.dashboard

import android.accessibilityservice.GestureDescription
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.data.local.TransactionDao
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
    // In DashboardViewModel
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _currentMonth = MutableStateFlow(LocalDate.now().monthValue)
    val currentMonth: StateFlow<Int> = _currentMonth

    // Filtered transactions based on the selected month
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        repository.allTransactions,
        _currentMonth
    ) { list, month ->
        list.filter { transaction ->
            val transactionMonth = Instant.ofEpochMilli(transaction.date)
                .atZone(ZoneId.systemDefault()).monthValue
            transactionMonth == month
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setMonth(month: Int) {
        _currentMonth.value = month
    }
    init{
        viewModelScope.launch {
            _isLoading.value=true
            kotlinx.coroutines.delay(500)
            _isLoading.value=false
        }
    }

    // Automatically listens to Room updates and recalculates totals instantly
    val transactions : StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun addTransaction(amount:Double,description: String,category: String,isExpense:Boolean,date: Long){
        viewModelScope.launch{
            repository.addManualTransaction(amount,description,category,isExpense,date)
        }


    }
    fun exportTransactions(onResult:(String)->Unit){
        viewModelScope.launch{
            val transactions = repository.allTransactions.first()
            val csvData= ExportRepository().generateCsv(transactions)
            onResult(csvData)

        }
    }

    val categoryTotals: StateFlow<Map<String, Double>> = transactions
        .map { list ->
            list.filter { it.isExpense }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun deleteTransaction(transactionId: Int) {
        viewModelScope.launch { repository.deleteTransaction(transactionId) }
    }
    fun updateTransaction(transaction: TransactionEntity) { viewModelScope.launch { repository.updateTransaction(transaction) } }

}
@Composable
fun SpendingSummaryChart(totals : Map<String,Double>){
    Column(modifier= Modifier.padding(16.dp)){
        Text("Spending by Category", style = MaterialTheme.typography.titleMedium)
        totals.forEach {(category,total)->
            Row(modifier= Modifier.fillMaxWidth().padding(vertical = 4.dp)){
                Text(category, modifier = Modifier.weight(1F))
                Text(text = "₹${total.toInt()}")
            }

        }

    }

}
