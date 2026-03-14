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

import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.databinding.ActivityMainBinding
import com.paisapulse.app.domain.models.CategorySum
import android.view.View

import com.paisapulse.app.presentation.ui.TransactionAdapter
import com.paisapulse.app.presentation.viewmodel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint // REQUIRED: This lets Hilt inject the ViewModel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val MONTHLY_LIMIT = 15000.0
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
        var navHostFragment= supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as androidx.navigation.fragment.NavHostFragment
        var navController = navHostFragment.navController

        // 3. Setup RecyclerView
        val adapter = TransactionAdapter()
        binding.rvTransactions.adapter = adapter

        // 4. Observe the Database (The Reactive Magic)
        viewModel.allTransactions.observe(this) { transactions: List<TransactionEntity> ->

            if (transactions.isEmpty()) {
                binding.rvTransactions.visibility = View.GONE
                binding.emptyStateLayout.visibility = View.VISIBLE
            } else {
                binding.rvTransactions.visibility = View.VISIBLE
                binding.emptyStateLayout.visibility = View.GONE
                adapter.submitList(transactions)
            }
        }
        binding.btnExport.setOnClickListener {
            // We use the ViewModel to get the non-flow list
            viewModel.getExportData { list ->
                if (list.isNotEmpty()) {
                    shareTransactionsCsv(list)
                } else {
                    Toast.makeText(this, "No data to export!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.chipGroupFilters.setOnCheckedChangeListener { _, checkedId ->
            // Remove previous observers to prevent multiple data streams clashing
            viewModel.allTransactions.removeObservers(this)
            viewModel.totalExpenses.removeObservers(this)

            when (checkedId) {
                R.id.chipAll -> {
                    viewModel.allTransactions.observe(this) { transactions: List<TransactionEntity> ->
                        adapter.submitList(transactions)
                    }
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
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase(Locale.getDefault())
                val fullList: List<TransactionEntity> = viewModel.allTransactions.value ?: emptyList()

                val filtered = fullList.filter { transaction ->
                    transaction.merchant.lowercase(Locale.getDefault()).contains(query) ||
                            transaction.category.lowercase(Locale.getDefault()).contains(query)
                }
                adapter.submitList(filtered)
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        viewModel.categoryTotals.observe(this) { totals ->
            if (!totals.isNullOrEmpty()) {
                setupChart(totals)
            }
        }
        // Simplified main observer
        viewModel.totalExpenses.observe(this) { total ->
            updateSummary(total)

            // Keep only the alert logic here
            val safeTotal = total ?: 0.0
            if ((safeTotal / MONTHLY_LIMIT) * 100 >= 100) {
                Toast.makeText(this, "🚨 Monthly Budget Exceeded!", Toast.LENGTH_SHORT).show()
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
        val safeTotal = total ?: 0.0
        binding.tvTotalAmount.text = String.format("₹%.2f", safeTotal)

        // Apply the "Traffic Light" color logic here too!
        val percentUsed = (safeTotal / MONTHLY_LIMIT) * 100
        val color = when {
            percentUsed >= 100 -> Color.parseColor("#B00020") // Red
            percentUsed >= 80 -> Color.parseColor("#F9A825")  // Orange
            else -> Color.parseColor("#6200EE")              // Purple
        }
        binding.cardSummary.setCardBackgroundColor(color)
    }
    private fun shareTransactionsCsv(transactions: List<TransactionEntity>) {
        val csvHeader = "Date,Merchant,Amount,Category\n"
        val csvBody = transactions.joinToString(separator = "\n") {
            "${it.timestamp},${it.merchant},${it.amount},${it.category}"
        }
        val fullCsv = csvHeader + csvBody

        try {
            // 1. Create temporary file
            val file = java.io.File(cacheDir, "PaisaPulse_Report.csv")
            file.writeText(fullCsv)

            // 2. Get Secure URI using FileProvider
            val contentUri = androidx.core.content.FileProvider.getUriForFile(
                this, "${packageName}.fileprovider", file
            )

            // 3. Create Share Intent
            val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(android.content.Intent.EXTRA_SUBJECT, "PaisaPulse Expense Report")
                putExtra(android.content.Intent.EXTRA_STREAM, contentUri)
                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(android.content.Intent.createChooser(intent, "Share Report via"))

        } catch (e: Exception) {
            Toast.makeText(this, "Export Failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}