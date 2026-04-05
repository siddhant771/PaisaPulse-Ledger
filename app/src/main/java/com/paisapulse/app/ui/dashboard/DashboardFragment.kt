package com.paisapulse.app.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.paisapulse.app.R
import com.paisapulse.app.databinding.FragmentDashboardBinding
import com.paisapulse.app.domain.models.CategorySum
import com.paisapulse.app.presentation.ui.TransactionAdapter
import com.paisapulse.app.presentation.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.toColorInt

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionViewModel by viewModels()
    private val transactionAdapter = TransactionAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        setupRecyclerView()
        setupPieChart()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.rvTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setEntryLabelColor(Color.WHITE)
            legend.isEnabled = false // Keep it clean for the Glassmorphism look
            animateY(1400)
        }
    }

    private fun observeViewModel() {
        // Total Amount Observer
        viewModel.totalExpenses.observe(viewLifecycleOwner) { total ->
            binding.tvTotalAmount.text = "₹ ${String.format("%.2f", total ?: 0.0)}"
        }

        // Transaction List Observer
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)

            // Toggle Empty State Visibility
            if (transactions.isNullOrEmpty()) {
                // Show empty state if no real data is found
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.rvTransactions.visibility = View.GONE
                binding.pieChart.visibility = View.INVISIBLE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.rvTransactions.visibility = View.VISIBLE
                binding.pieChart.visibility = View.VISIBLE
                transactionAdapter.submitList(transactions)
            }
        }

        // Pie Chart Observer
        viewModel.categoryTotals.observe(viewLifecycleOwner) { totals ->
            updateChart(totals)
        }
    }

    private fun updateChart(totals: List<CategorySum>) {
        if (totals.isEmpty()) return

        val entries = totals.map { PieEntry(it.total.toFloat(), it.category) }

        val dataSet = PieDataSet(entries, "Expenses").apply {
            // Define colors and handle the color list safely
            val chartColors = listOf(
                Color.parseColor("#FF9800"), // Food
                Color.parseColor("#2196F3"), // Travel
                Color.parseColor("#F44336"), // Bills
                Color.parseColor("#9C27B0")  // Others
            )
            colors = chartColors
            sliceSpace = 3f
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.invalidate()
    }
}