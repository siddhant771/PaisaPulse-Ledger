package com.paisapulse.app.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.paisapulse.app.domain.repository.TransactionRepository
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.domain.models.CategorySum

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    // Converting Flow to LiveData for the XML View
    val allTransactions = repository.getAllTransactions().asLiveData()
    val totalExpenses: LiveData<Double?> = repository.getTotalExpenses().asLiveData()
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
    fun getFilteredData(category: String): LiveData<List<TransactionEntity>> {
        return repository.getTransactionsByCategory(category).asLiveData()
    }

    fun getFilteredTotal(category: String): LiveData<Double?> {
        return repository.getTotalByCategory(category).asLiveData()
    }
    val categoryTotals: LiveData<List<CategorySum>> = repository.getCategoryTotals().asLiveData()

}