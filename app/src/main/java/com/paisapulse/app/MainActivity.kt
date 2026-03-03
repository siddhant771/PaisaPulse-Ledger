package com.paisapulse.app

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.paisapulse.app.databinding.ActivityMainBinding
import com.paisapulse.app.domain.models.CategorySum

import com.paisapulse.app.presentation.ui.TransactionAdapter
import com.paisapulse.app.presentation.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // REQUIRED: This lets Hilt inject the ViewModel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TransactionViewModel by viewModels()

    // 1. Permission Logic
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Tracking Enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Denied. Auto-tracking won't work.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Setup RecyclerView
        val adapter = TransactionAdapter()
        binding.rvTransactions.adapter = adapter

        // 4. Observe the Database (The Reactive Magic)
        viewModel.allTransactions.observe(this) { transactions ->
            // DiffUtil works behind the scenes here to only refresh changed items
            adapter.submitList(transactions)
        }
        viewModel.totalExpenses.observe(this) { total ->
            val safeTotal = total ?: 0.0
            binding.tvTotalAmount.text = String.format("₹%.2f", safeTotal)
        }
        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            // Remove previous observers to prevent multiple data streams clashing
            viewModel.allTransactions.removeObservers(this)
            viewModel.totalExpenses.removeObservers(this)

            when (checkedId) {
                R.id.chipAll -> {
                    viewModel.allTransactions.observe(this) { adapter.submitList(it) }
                    viewModel.totalExpenses.observe(this) { updateSummary(it) }
                }
                R.id.chipFood -> {
                    // These require the ViewModel updates in Step 2 below
                    viewModel.getFilteredData("Food").observe(this) { adapter.submitList(it) }
                    viewModel.getFilteredTotal("Food").observe(this) { updateSummary(it) }
                }
                R.id.chipTravel -> {
                    viewModel.getFilteredData("Travel").observe(this) { adapter.submitList(it) }
                    viewModel.getFilteredTotal("Travel").observe(this) { updateSummary(it) }
                }
                R.id.chipBills -> {
                    viewModel.getFilteredData("Bills").observe(this) { adapter.submitList(it) }
                    viewModel.getFilteredTotal("Bills").observe(this) { updateSummary(it) }
                }
            }
        }

        viewModel.categoryTotals.observe(this) { totals ->
            if (!totals.isNullOrEmpty()) {
                setupChart(totals)
            }
        }




        // 5. Ask for Permission on startup
        requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        val swipeHandler = object : androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT) {
            override fun onMove(rv: androidx.recyclerview.widget.RecyclerView, vh: androidx.recyclerview.widget.RecyclerView.ViewHolder, t: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val transaction = adapter.currentList[position] // Get the swiped item
                viewModel.deleteTransaction(transaction) // Delete from DB
                Toast.makeText(this@MainActivity, "Transaction Deleted", Toast.LENGTH_SHORT).show()
            }
        }

        androidx.recyclerview.widget.ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvTransactions)
    }
    private fun setupChart(categorySums: List<CategorySum>) {
        val entries = categorySums.map {
            PieEntry(it.total.toFloat(), it.category)
        }

        val dataSet = PieDataSet(entries, "Spending").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }

        binding.pieChart.apply {
            data = PieData(dataSet)
            centerText = "Expenses"
            setCenterTextSize(18f)
            description.isEnabled = false
            animateY(1000) // Beautiful entrance animation
            invalidate() // Refresh chart
        }
    }
    private fun updateSummary(total: Double?) {
        binding.tvTotalAmount.text = String.format("₹%.2f", total ?: 0.0)
    }
}