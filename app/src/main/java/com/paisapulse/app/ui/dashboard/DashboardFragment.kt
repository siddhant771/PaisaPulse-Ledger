package com.paisapulse.app.ui.dashboard

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.paisapulse.app.R
import com.paisapulse.app.databinding.FragmentDashboardBinding
import com.paisapulse.app.domain.models.CategorySum
import com.paisapulse.app.presentation.ui.TransactionAdapter
import com.paisapulse.app.presentation.viewmodel.TransactionViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        setupNavigationActions()
        observeViewModel()
        observeBudgets()
    }

    private fun setupNavigationActions() {
        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_search)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_settings)
        }
        binding.btnAiCoach.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_aiCoach)
        }
        binding.btnExport.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_export)
        }
        binding.btnAiInsights.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_aiInsights)
        }
    }

    private fun setupRecyclerView() {
        binding.rvTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) return
                val transaction = transactionAdapter.currentList[position]
                viewModel.deleteTransaction(transaction)
                Snackbar.make(binding.root, "Transaction Deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        viewModel.insertTransaction(transaction.copy(id = 0))
                    }
                    .show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvTransactions)
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
        // Total Amount Observer - Initialize to 0
        binding.tvTotalAmount.text = "₹ 0.00"
        viewModel.totalExpenses.observe(viewLifecycleOwner) { total ->
            binding.tvTotalAmount.text = "₹ ${String.format("%.2f", total ?: 0.0)}"
        }

        // Transaction List Observer
        // Inside observeViewModel()
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            val listToShow = transactions ?: emptyList()

            // Force new list reference for DiffUtil
            transactionAdapter.submitList(listToShow.toList())
            viewModel.generateInsights(listToShow)

            if (listToShow.isEmpty()) {
                binding.emptyStateLayout.visibility = View.VISIBLE
                binding.rvTransactions.visibility = View.GONE
                binding.pieChart.visibility = View.INVISIBLE
            } else {
                binding.emptyStateLayout.visibility = View.GONE
                binding.rvTransactions.visibility = View.VISIBLE
                binding.pieChart.visibility = View.VISIBLE

                // Ensure UI stays at top for new messages
                binding.rvTransactions.scrollToPosition(0)
            }
        }

        // Pie Chart Observer
        viewModel.categoryTotals.observe(viewLifecycleOwner) { totals ->
            updateChart(totals)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.aiInsight.collect { insight ->
                binding.tvAiInsight.text = insight?.description ?: "AI is analyzing your spends..."
            }
        }
    }

    private fun observeBudgets() {
        // Initialize budget values to 0
        binding.foodProgressBar.progress = 0
        binding.travelProgressBar.progress = 0
        binding.billsProgressBar.progress = 0
        binding.tvFoodLimit.text = "₹0 / ₹0"
        binding.tvTravelLimit.text = "₹0 / ₹0"
        binding.tvBillsLimit.text = "₹0 / ₹0"
        
        viewModel.budgetStatuses.observe(viewLifecycleOwner) { statuses ->
            statuses.forEach { status ->
                val color = if (status.isOverThreshold) "#F44336" else "#800080"
                val summaryText = "₹${String.format("%.2f", status.spentAmount)} / ₹${String.format("%.2f", status.limitAmount)}"

                when (status.category.lowercase()) {
                    "food" -> {
                        binding.foodProgressBar.progress = status.percentUsed
                        binding.foodProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor(color))
                        binding.tvFoodLimit.text = summaryText
                    }

                    "travel" -> {
                        binding.travelProgressBar.progress = status.percentUsed
                        binding.travelProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor(color))
                        binding.tvTravelLimit.text = summaryText
                    }

                    "bills" -> {
                        binding.billsProgressBar.progress = status.percentUsed
                        binding.billsProgressBar.progressTintList = ColorStateList.valueOf(Color.parseColor(color))
                        binding.tvBillsLimit.text = summaryText
                    }
                }
            }
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