package com.paisapulse.app.ui.aiinsights

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paisapulse.app.R
import com.paisapulse.app.databinding.FragmentAiInsightsBinding

class AiInsightsFragment : Fragment(R.layout.fragment_ai_insights) {
    private var _binding: FragmentAiInsightsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiInsightsBinding.bind(view)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
