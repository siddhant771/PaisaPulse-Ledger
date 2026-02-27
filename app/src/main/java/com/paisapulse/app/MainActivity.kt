package com.paisapulse.app

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.paisapulse.app.databinding.ActivityMainBinding
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
}