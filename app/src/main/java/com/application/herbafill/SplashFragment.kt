package com.application.herbafill

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.application.herbafill.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)

        // Set initial alpha to 0 (completely transparent)
        binding.root.alpha = 0f

        // Start the fade-in animation
        binding.root.animate()
            .alpha(1f) // Fade in to fully opaque
            .setDuration(1000) // Duration of the fade-in effect
            .withEndAction {
                // Start the timer after fade-in is complete
                startTimer()
            }

        return binding.root
    }

    private fun startTimer() {
        val timer = object : CountDownTimer(3000, 20) {
            override fun onTick(millisUntilFinished: Long) {
                // You can update UI here if needed
            }

            override fun onFinish() {
                // Navigate to the next fragment after the timer is done
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
        timer.start()
    }
}
