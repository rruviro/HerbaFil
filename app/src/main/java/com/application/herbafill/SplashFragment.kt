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

        binding.root.alpha = 0f

        binding.root.animate()
            .alpha(1f)
            .setDuration(1000)
            .withEndAction {
                startTimer()
            }

        return binding.root
    }

    private fun startTimer() {
        val timer = object : CountDownTimer(3000, 20) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }
        timer.start()
    }
}
