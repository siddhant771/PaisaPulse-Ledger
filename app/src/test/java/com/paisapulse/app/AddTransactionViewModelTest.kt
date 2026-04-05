package com.paisapulse.app

import com.paisapulse.app.domain.repository.ITransactionRepository
import com.paisapulse.app.presentation.viewmodel.AddTransactionViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddTransactionViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddTransactionViewModel
    private val repository: ITransactionRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        viewModel = AddTransactionViewModel(repository)
    }

    @Test

    fun testWhenAmountIsZeroEmitError() = runTest {
        val results = mutableListOf<AddTransactionViewModel.UiEvent>()

        // Using backgroundScope ensures the collector stays alive during the test
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.eventFlow.collect { results.add(it) }
        }

        // Trigger action
        viewModel.onSaveClicked("0.0", "Food")

        // Check results
        val hasError = results.any { it is AddTransactionViewModel.UiEvent.ShowError }
        assert(hasError) { "Expected ShowError event but received: $results" }
    }
}