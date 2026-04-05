package com.paisapulse.app.ui.splash



import android.os.Bundle
import android.widget.Button
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.paisapulse.app.R
import dagger.hilt.android.AndroidEntryPoint


import com.paisapulse.app.databinding.FragmentSplashBinding

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)

        binding.btnGetStarted.setOnClickListener {
            // Move to the Permission screen as per Figma flow
            findNavController().navigate(R.id.action_splash_to_permission)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}