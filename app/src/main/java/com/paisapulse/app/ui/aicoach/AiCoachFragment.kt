package com.paisapulse.app.ui.aicoach

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paisapulse.app.R
import com.paisapulse.app.databinding.FragmentAiCoachBinding

class AiCoachFragment : Fragment(R.layout.fragment_ai_coach) {
    private var _binding: FragmentAiCoachBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiCoachBinding.bind(view)

        // Back navigation
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        
        // Suggested questions click handlers
        // Note: These would need to be added to the layout as actual buttons
        // For now, the layout contains MaterialButtons that will work
        
        // Send message functionality
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                // Handle sending message
                binding.etMessage.text.clear()
            }
        }
        
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
