package com.paisapulse.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.repository.ITransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: ITransactionRepository
) : ViewModel() {

    // Used for showing the "Success" Toast
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onSaveClicked(amountStr: String, category: String) {
        val amount = amountStr.toDoubleOrNull() ?: 0.0

        // SDE-3 Validation: Never trust the UI input
        if (amount <= 0) {
            viewModelScope.launch { _eventFlow.emit(UiEvent.ShowError("Enter valid amount")) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val totalSpentSoFar = repository.getAllTransactionsList().sumOf { transaction: TransactionEntity ->
                transaction.amount
            }
            val startingBudget = 15000.0

            // THIS IS THE MISSING LINE:
            val calculatedBalance = startingBudget - (totalSpentSoFar + amount)
            val transaction = TransactionEntity(
                amount = amount,
                category = category,
                merchant = "Manual Entry",       // Default for manual adds
                timestamp = System.currentTimeMillis(), // Current time
                rawBody = "User added manually",
                currentBalance = calculatedBalance
            )
            repository.insertTransaction(transaction)
            _eventFlow.emit(UiEvent.SaveSuccess)
        }
    }

    sealed class UiEvent {
        object SaveSuccess : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }
}