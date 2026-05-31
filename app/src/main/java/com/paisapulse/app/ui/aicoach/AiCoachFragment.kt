package com.paisapulse.app.ui.aicoach

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.paisapulse.app.R
import com.paisapulse.app.databinding.FragmentAiCoachBinding
import com.paisapulse.app.presentation.ui.ChatMessageAdapter
import com.paisapulse.app.presentation.viewmodel.AiCoachViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AiCoachFragment : Fragment(R.layout.fragment_ai_coach) {
    private var _binding: FragmentAiCoachBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AiCoachViewModel by viewModels()
    private val chatAdapter = ChatMessageAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiCoachBinding.bind(view)

        setupChatRecyclerView()
        setupNavigationActions()
        observeViewModel()
    }

    private fun setupChatRecyclerView() {
        binding.rvChat.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            setHasFixedSize(false)
        }
    }

    private fun setupNavigationActions() {
        // Back navigation
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        // Send message functionality
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.etMessage.text.clear()
            }
        }

        // Suggested questions - these will be populated dynamically
        // Click handlers for question buttons if they exist in layout

        // Bottom navigation
        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_aiCoach_to_dashboard)
        }

        binding.btnChat.setOnClickListener {
            // Already on AI Coach screen
        }

        binding.btnExport.setOnClickListener {
            findNavController().navigate(R.id.action_aiCoach_to_export)
        }
    }

    private fun observeViewModel() {
        // Observe chat messages
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            chatAdapter.submitList(messages)
            // Scroll to latest message
            if (messages.isNotEmpty()) {
                binding.rvChat.scrollToPosition(messages.size - 1)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnSend.isEnabled = !isLoading
            binding.etMessage.isEnabled = !isLoading
        }

        // Observe suggested questions
        viewModel.suggestedQuestions.observe(viewLifecycleOwner) { questions ->
            setupSuggestedQuestions(questions)
        }
    }

    private fun setupSuggestedQuestions(questions: List<String>) {
        // These would be added dynamically to the layout
        // For now, we'll handle clicks if buttons are pre-added in the layout
        if (questions.isNotEmpty()) {
            // If there are question buttons in the layout, set up click listeners
            // This depends on how the layout is structured
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
