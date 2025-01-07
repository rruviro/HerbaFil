package com.application.herbafill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Authentication.SignUpResponse
import com.application.herbafill.Model.OTPVerifyRequest
import com.application.herbafill.Model.OTPVerifyResponse
import com.application.herbafill.Model.UserImage
import com.application.herbafill.databinding.FragmentOtpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOtpBinding.inflate(inflater, container, false)

        // Initialize Firebase database reference
        database = FirebaseDatabase.getInstance().reference.child("users")

        val name = arguments?.getString("name") ?: return binding.root
        val email = arguments?.getString("email") ?: return binding.root
        val username = arguments?.getString("username") ?: return binding.root
        val password = arguments?.getString("password") ?: return binding.root

        binding.verifyButton.setOnClickListener {
            val otp = binding.otp.text.toString()
            if (otp.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verifyOTP(name, email, username, password, otp)
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_otpFragment_to_registerFragment)
        }

        return binding.root
    }

    private fun verifyOTP(name: String, email: String, username: String, password: String, otp: String) {
        val otpService = RetrofitClient.instance
        val call = otpService.verifyOTP(OTPVerifyRequest(email, otp))

        call.enqueue(object : Callback<OTPVerifyResponse> {
            override fun onResponse(call: Call<OTPVerifyResponse>, response: Response<OTPVerifyResponse>) {
                if (response.isSuccessful) {
                    registerUser(name, email, username, password)
                    saveImage(email, "") // No issue with database initialization now
                } else {
                    Toast.makeText(requireContext(), "Invalid OTP or OTP expired", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OTPVerifyResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerUser(name: String, email: String, username: String, password: String) {
        RetrofitClient.instance.signUp(name, email, username, password)
            .enqueue(object : Callback<SignUpResponse> {
                override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.success) {
                            Toast.makeText(context, "Sign-up successful!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_otpFragment_to_loginFragment)
                        } else {
                            Toast.makeText(context, responseBody?.message ?: "Sign-up failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveImage(
        email: String,
        userProfile: String
    ) {
        val sanitizedEmail = email.replace(".", "_") // Sanitizing email

        val userProfileMap = mapOf(
            "userProfile" to userProfile // Map the user profile URL under the key "userProfile"
        )

        // Firebase Database reference
        database.child(sanitizedEmail).setValue(userProfileMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "User data saved successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "Error saving data", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
