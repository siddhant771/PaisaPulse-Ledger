package com.paisapulse.app.ui.dashboard

import com.paisapulse.app.data.local.TransactionEntity

data class DashboardUiState(
    val totalExpenses: Double = 0.0,
    val totalIncome: Double = 0.0,
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = true
)