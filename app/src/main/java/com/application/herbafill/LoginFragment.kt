package com.application.herbafill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Authentication.LoginResponse
import com.application.herbafill.databinding.FragmentLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {

            val username = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(username, password)

        }

        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }

    private fun loginUser(username: String, password: String) {
        RetrofitClient.instance.loginUser(username, password)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        if (loginResponse?.status == "success") {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            // Proceed to next activity
                        } else {
                            Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(context, "Connection Failed", Toast.LENGTH_SHORT).show()
                }

            })
    }

}