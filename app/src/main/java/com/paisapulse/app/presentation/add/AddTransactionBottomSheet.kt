package com.paisapulse.app.presentation.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.chip.Chip
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paisapulse.app.databinding.LayoutAddTransactionBinding
import com.paisapulse.app.presentation.viewmodel.AddTransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddTransactionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: LayoutAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddTransactionViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val amount = binding.etAmount.text.toString()
            val selectedChipId = binding.cgCategories.checkedChipId
            val category = if (selectedChipId != View.NO_ID) {
                binding.root.findViewById<Chip>(selectedChipId).text.toString()
            } else {
                "Other"
            }
            viewModel.onSaveClicked(amount, category)
        }

        // SDE-3 Level: Listening for One-Time Events
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collect { event ->
                    when (event) {
                        is AddTransactionViewModel.UiEvent.SaveSuccess -> {
                            dismiss() // Close the bottom sheet
                        }
                        is AddTransactionViewModel.UiEvent.ShowError -> {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}