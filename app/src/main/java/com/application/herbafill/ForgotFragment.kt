package com.application.herbafill

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.OTPForgotVerifyRequest
import com.application.herbafill.Model.OTPForgotVerifyResponse
import com.application.herbafill.Model.OTPRequest
import com.application.herbafill.Model.OTPResponse
import com.application.herbafill.Model.OTPVerifyRequest
import com.application.herbafill.Model.OTPVerifyResponse
import com.application.herbafill.Model.ResetPasswordRequest
import com.application.herbafill.Model.ResetPasswordResponse
import com.application.herbafill.Model.UpdateResponse
import com.application.herbafill.databinding.FragmentForgotBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotFragment : Fragment() {

    private lateinit var binding: FragmentForgotBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotBinding.inflate(inflater, container, false)

        binding.getOtpBtn.setOnClickListener {
            val email = binding.email.text.trim()
            sendOTP(email.toString())
        }

        return binding.root
    }

    private fun sendOTP(email: String) {
        val otpService = RetrofitClient.instance
        val call = otpService.forgotSendOTP(OTPRequest(email))

        call.enqueue(object : Callback<OTPResponse> {
            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                if (response.isSuccessful) {
                    binding.getOtpBtn.visibility = View.GONE
                    binding.verifyButton.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show()
                    verifyOTP(email)
                } else {
                    Toast.makeText(requireContext(), "Try Again!", Toast.LENGTH_SHORT).show()
                    println("Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyOTP(email: String) {
        binding.verifyButton.setOnClickListener {

            val otp = binding.otp.text.trim().toString()
            val otpService = RetrofitClient.instance
            val call = otpService.forgotVerifyOTP(OTPForgotVerifyRequest(email, otp)) // Create a new Call instance

            call.enqueue(object : Callback<OTPForgotVerifyResponse> {
                override fun onResponse(call: Call<OTPForgotVerifyResponse>, response: Response<OTPForgotVerifyResponse>) {
                    if (response.isSuccessful) {
                        val dialogView = layoutInflater.inflate(R.layout.forgotpass, null)

                        val dialogBuilder = AlertDialog.Builder(requireContext())
                            .setView(dialogView)

                        val passwordEditText = dialogView.findViewById<EditText>(R.id.password)
                        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.confirm_password)
                        val eyeToggle = dialogView.findViewById<TextView>(R.id.eyeToggle)
                        val deployButton = dialogView.findViewById<TextView>(R.id.deploy)

                        val dialog = dialogBuilder.create()

                        eyeToggle.setOnClickListener {
                            val isPasswordVisible = passwordEditText.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)

                            if (isPasswordVisible) {
                                // Hide password
                                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                            } else {
                                // Show password
                                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                                confirmPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            }
                            passwordEditText.setSelection(passwordEditText.text.length)
                            confirmPasswordEditText.setSelection(confirmPasswordEditText.text.length)
                        }

                        deployButton.setOnClickListener {
                            val password = passwordEditText.text.toString()
                            val confirmPassword = confirmPasswordEditText.text.toString()

                            if (password == confirmPassword) {
                                resetPassword(email, password) // Call your resetPassword function
                                dialog.dismiss()
                            } else {
                                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                            }
                        }

                        dialog.show()
                    } else {
                        Toast.makeText(requireContext(), "Invalid OTP or OTP expired", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OTPForgotVerifyResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun resetPassword(email: String, newPassword: String) {
        val request = ResetPasswordRequest(email, newPassword)

        RetrofitClient.instance.resetPassword(request).enqueue(object : Callback<ResetPasswordResponse> {
            override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        findNavController().navigate(R.id.action_forgotFragment_to_loginFragment)
                        Toast.makeText(requireContext(), "Password reset successfully", Toast.LENGTH_SHORT).show()
                    } ?: Toast.makeText(context, "Response body is null", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to reset password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                Toast.makeText(context, "Request failed: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}