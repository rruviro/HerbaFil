package com.application.herbafill

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Authentication.SignUpResponse
import com.application.herbafill.Model.CreateAccountRequest
import com.application.herbafill.Model.CreateAccountResponse
import com.application.herbafill.Model.OTPRequest
import com.application.herbafill.Model.OTPResponse
import com.application.herbafill.Model.UserImage
import com.application.herbafill.databinding.FragmentRegisterBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.eyeToggle.setOnClickListener {
            val currentType = binding.password.inputType
            if (currentType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.confirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.eyeToggle.setBackgroundResource(R.drawable.ic_eye_open)
            } else {
                binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.confirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.eyeToggle.setBackgroundResource(R.drawable.ic_eye_closed)
            }
            binding.password.setSelection(binding.password.text.length)
        }

        binding.registerButton.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "You're required to input your credentials.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(requireContext(), "Password must be between 8 and 16 characters, and contain at least one uppercase letter, one lowercase letter, and one symbol.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkable(name, email, username, password)
        }

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }

    fun isValidPassword(password: String): Boolean {
        val regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{8,16}$"
        return password.matches(regex.toRegex())
    }

    fun checkable(name: String, email: String, username: String, password: String) {
        // Create request body
        val request = CreateAccountRequest(name, email, username, password)
        // Make API call
        RetrofitClient.instance.checkable(request).enqueue(object : Callback<CreateAccountResponse> {
            override fun onResponse(
                call: Call<CreateAccountResponse>,
                response: Response<CreateAccountResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.success) {
                            val bundle = Bundle().apply {
                                putString("name", name)
                                putString("email", email)
                                putString("username", username)
                                putString("password", password)
                            }
                            sendOTP(email)
                            findNavController().navigate(R.id.action_registerFragment_to_otpFragment, bundle)
                        } else {
                            // Handle failure (Username or email already exists)
                            Toast.makeText(requireContext(), responseBody.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle server error
                    println("Server error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CreateAccountResponse>, t: Throwable) {
                // Handle failure
                println("Network error: ${t.message}")
            }
        })
    }

    private fun sendOTP(email: String) {
        val otpService = RetrofitClient.instance
        val call = otpService.sendOTP(OTPRequest(email))

        call.enqueue(object : Callback<OTPResponse> {
            override fun onResponse(call: Call<OTPResponse>, response: Response<OTPResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP sent successfully", Toast.LENGTH_SHORT).show()
                    println("OTP: ${response.body()?.otp}")
                } else {
                    Toast.makeText(requireContext(), "Failed to send OTP: ${response.message()}", Toast.LENGTH_SHORT).show()
                    println("Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OTPResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}