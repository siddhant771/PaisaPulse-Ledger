package com.paisapulse.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.data.local.TransactionDao
import com.paisapulse.app.data.local.TransactionEntity
import com.paisapulse.app.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    // Automatically listens to Room updates and recalculates totals instantly
    val transactions : StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun addTransaction(transaction: TransactionEntity){
        viewModelScope.launch{
            repository.insertTransaction(transaction)
        }
    }
}