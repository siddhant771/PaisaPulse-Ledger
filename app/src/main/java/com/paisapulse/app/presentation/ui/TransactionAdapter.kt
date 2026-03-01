package com.paisapulse.app.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
        fun bind(item: TransactionEntity) {
            binding.tvMerchant.text = item.merchant

            // 1. Format Currency to 2 decimal places (e.g., ₹500.00)
            binding.tvAmount.text = String.format("₹%.2f", item.amount)


            // 2. Convert timestamp to a readable Date (e.g., 27 Feb, 09:10 PM)
            val sdf = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault())
            val dateString = sdf.format(java.util.Date(item.timestamp))
            binding.tvDate.text = "${item.category} • $dateString"

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity) = oldItem == newItem
    }
}