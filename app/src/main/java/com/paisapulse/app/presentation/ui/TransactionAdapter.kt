package com.paisapulse.app.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paisapulse.app.R
import com.paisapulse.app.data.local.entities.TransactionEntity
import com.paisapulse.app.databinding.ItemTransactionBinding

class TransactionAdapter : ListAdapter<TransactionEntity, TransactionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity) {
            binding.tvMerchant.text = transaction.merchant
            binding.tvAmount.text = "₹${transaction.amount}"

            // Logic for dynamic icons
            val iconRes = when (transaction.category) {
                "Food" -> R.drawable.ic_restaurant
                "Travel" -> R.drawable.ic_directions_car
                "Bills" -> R.drawable.ic_receipt
                else -> R.drawable.ic_payment
            }

            binding.ivCategoryIcon.setImageResource(iconRes)

            // Pro Tip: Change icon color based on category
            val iconColor = when (transaction.category) {
                "Food" -> "#FF9800" // Orange
                "Travel" -> "#2196F3" // Blue
                "Bills" -> "#F44336" // Red
                else -> "#9C27B0" // Purple
            }
            binding.ivCategoryIcon.setColorFilter(android.graphics.Color.parseColor(iconColor))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity) = oldItem == newItem
    }
}