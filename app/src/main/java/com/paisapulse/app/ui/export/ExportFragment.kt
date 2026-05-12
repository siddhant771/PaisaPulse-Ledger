package com.paisapulse.app.ui.export

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.paisapulse.app.R
import com.paisapulse.app.databinding.FragmentExportBinding
import com.paisapulse.app.presentation.viewmodel.TransactionViewModel
import com.paisapulse.app.utils.ExportProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExportFragment : Fragment(R.layout.fragment_export) {
    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExportBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnExportCsv.setOnClickListener {
            val data = viewModel.allTransactions.value.orEmpty()
            if (data.isEmpty()) {
                Toast.makeText(requireContext(), "No transactions to export", Toast.LENGTH_SHORT).show()
            } else {
                ExportProvider.exportToCSV(data, requireContext())
            }
        }

        binding.btnExportPdf.setOnClickListener {
            Toast.makeText(requireContext(), "PDF export will be added next", Toast.LENGTH_SHORT).show()
        }
        
        // Bottom navigation
        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_export_to_dashboard)
        }
        
        binding.btnChat.setOnClickListener {
            findNavController().navigate(R.id.action_export_to_aiCoach)
        }
        
        binding.btnExport.setOnClickListener {
            // Already on Export screen
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
