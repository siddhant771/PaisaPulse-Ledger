package com.paisapulse.app.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paisapulse.app.R
import com.paisapulse.app.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        
        // Back navigation
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        
        // Dark mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Handle dark mode toggle
        }
        
        // Notifications toggle
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // Handle notifications toggle
        }
        
        // Navigation items
        binding.btnCurrency.setOnClickListener {
            // Navigate to currency selection
        }
        
        binding.btnSmsPermissions.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_permission)
        }
        
        binding.btnDataPrivacy.setOnClickListener {
            // Navigate to data privacy
        }
        
        binding.btnAppInfo.setOnClickListener {
            // Navigate to app info
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
