package com.paisapulse.app.presentation.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.paisapulse.app.R
import com.paisapulse.app.databinding.ItemChatMessageBinding
import com.paisapulse.app.presentation.viewmodel.ChatMessage

class ChatMessageAdapter : ListAdapter<ChatMessage, ChatMessageAdapter.ChatMessageViewHolder>(
    ChatMessageDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        return ChatMessageViewHolder(
            ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatMessageViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            binding.tvMessage.text = message.text
            binding.tvTimestamp.text = message.timestamp

            if (message.isUserMessage) {
                // User message - right aligned with purple background
                binding.messageCard.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.purple_brand)
                )
                binding.tvMessage.setTextColor(Color.WHITE)
                binding.tvTimestamp.setTextColor(Color.parseColor("#B3FFFFFF"))
                
                // Right align the card
                val params = binding.messageContainer.layoutParams as? FrameLayout.LayoutParams
                if (params != null) {
                    params.gravity = android.view.Gravity.END
                    binding.messageContainer.layoutParams = params
                }
            } else {
                // AI message - left aligned with white background
                binding.messageCard.setCardBackgroundColor(Color.WHITE)
                binding.tvMessage.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_primary)
                )
                binding.tvTimestamp.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.text_secondary)
                )
                
                // Left align the card
                val params = binding.messageContainer.layoutParams as? FrameLayout.LayoutParams
                if (params != null) {
                    params.gravity = android.view.Gravity.START
                    binding.messageContainer.layoutParams = params
                }
            }
        }
    }

    private class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage) =
            oldItem == newItem
    }
}
