package com.paisapulse.app

import com.paisapulse.app.domain.repository.ITransactionRepository
import com.paisapulse.app.presentation.viewmodel.AddTransactionViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
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
        // 1. Listen to the SharedFlow
        val results = mutableListOf<AddTransactionViewModel.UiEvent>()

        // Use the correct Coroutine launch, NOT Activity launch
        val job = launch {
            viewModel.eventFlow.toList(results)
        }

        // 2. Trigger the action
        viewModel.onSaveClicked("0.0", "Food")

        // 3. Verify
        assert(results.any { it is AddTransactionViewModel.UiEvent.ShowError })

        job.cancel()
    }
}