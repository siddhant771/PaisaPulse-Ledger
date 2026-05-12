package com.paisapulse.app.presentation.dashboard

import com.paisapulse.app.data.local.entities.TransactionEntity

/**
 * DEPRECATED - Use com.paisapulse.app.ui.dashboard.DashboardUiState instead
 * This is a duplicate file that should be removed
 */
@Deprecated("Use com.paisapulse.app.ui.dashboard.DashboardUiState instead")
data class DashboardUiState(
    val isLoading: Boolean = true,
    val totalBalance: String = "₹ 0.00",
    val transactions: List<TransactionEntity> = emptyList(),
    val errorMessage: String? = null
)
