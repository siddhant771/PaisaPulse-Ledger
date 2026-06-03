package com.paisapulse.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.data.local.TransactionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {

    // Automatically listens to Room updates and recalculates totals instantly
    val uiState: StateFlow<DashboardUiState> = transactionDao.getAllTransactions()
        .map { transactions ->
            val expenses = transactions.filter { it.category != "Income" }.sumOf { it.amount }
            val income = transactions.filter { it.category == "Income" }.sumOf { it.amount }

            DashboardUiState(
                totalExpenses = expenses,
                totalIncome = income,
                recentTransactions = transactions.take(20), // Show top 20 latest events
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState()
        )
}