package com.paisapulse.app.ui.permission



import androidx.fragment.app.Fragment
import com.paisapulse.app.R


import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import androidx.navigation.fragment.findNavController

import com.paisapulse.app.databinding.FragmentPermissionBinding

class PermissionFragment : Fragment(R.layout.fragment_permission) {

    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    // Modern Android API for requesting permissions
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            navigateToDashboard()
        } else {
            showDeniedState()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPermissionBinding.bind(view)

        resetPermissionScreen()

        binding.btnAllow.setOnClickListener {
            checkAndRequestPermissions()
        }
        binding.btnNotNow.setOnClickListener {
            navigateToDashboard()
        }
        binding.tvOfflineHint.visibility = if (isOffline()) View.VISIBLE else View.GONE
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
        )

        if (permissions.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }) {
            navigateToDashboard()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    private fun navigateToDashboard() {
        findNavController().navigate(R.id.action_permission_to_dashboard)
    }

    private fun showDeniedState() {
        binding.tvPermissionTitle.text = "SMS Permission Denied"
        binding.tvPermissionDesc.text =
            "FinSecure needs SMS access to automatically track your transactions. Without this permission, the app will still work with manual entry."
        binding.btnAllow.text = "Grant Permission"
        binding.btnNotNow.text = "Continue with Manual Entry"
        Toast.makeText(requireContext(), "Permission denied. You can enable it later from settings.", Toast.LENGTH_LONG).show()
    }

    private fun resetPermissionScreen() {
        binding.tvPermissionTitle.text = "SMS Permission Required"
        binding.tvPermissionDesc.text =
            "FinSecure needs access to your SMS messages to automatically track your transactions from bank notifications."
        binding.btnAllow.text = "Allow SMS Access"
        binding.btnNotNow.text = "Not Now"
    }

    private fun isOffline(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return true
        val caps = cm.getNetworkCapabilities(network) ?: return true
        return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}