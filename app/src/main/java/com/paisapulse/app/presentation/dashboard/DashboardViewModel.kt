package com.paisapulse.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.domain.repository.ITransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ITransactionRepository
) : ViewModel() {

    // SDE-3 Depth: Converting Double? to a Formatted String State
    val totalBalanceState: StateFlow<String> = repository.getTotalExpenses()
        .map { amount -> "₹ ${String.format("%.2f", amount ?: 0.0)}" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Resource optimization
            initialValue = "₹ 0.00"
        )
}