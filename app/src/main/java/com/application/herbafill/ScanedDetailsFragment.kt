package com.application.herbafill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.herbafill.Adapter.MLHerbalBenifitAdapter
import com.application.herbafill.Adapter.MLHerbalStepsAdapter
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Authentication.SignUpResponse
import com.application.herbafill.Model.MLBenefitsResponse
import com.application.herbafill.Model.MLDetailsResponse
import com.application.herbafill.Model.MLStepsResponse
import com.application.herbafill.Model.UserHistory
import com.application.herbafill.databinding.FragmentScanedDetailsBinding
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScanedDetailsFragment : Fragment() {

    private lateinit var binding: FragmentScanedDetailsBinding
    private lateinit var benefitAdapter: MLHerbalBenifitAdapter
    private lateinit var stepsAdapter: MLHerbalStepsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanedDetailsBinding.inflate(inflater, container, false)
        val mlHerbName = arguments?.getString("mlHerbName") ?: return binding.root
        val userID = arguments?.getInt("userID") ?: return binding.root
        val childBundle = Bundle()
        childBundle.putInt("userID", userID)

        binding.herbTitle.text = mlHerbName
        binding.HerbTitle.text = mlHerbName

        fetchAndDisplayHerbalData(mlHerbName)
        fetchHerbalBenefits(mlHerbName)
        fetchHerbalSteps(mlHerbName)

        val userHistory = UserHistory(userID = userID, mlherbname = mlHerbName)
        insert(userHistory)

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_scanedDetailsFragment_to_homeFragment, childBundle)
        }

        return binding.root
    }

    private fun fetchAndDisplayHerbalData(mlHerbName: String) {
        RetrofitClient.instance.getHerbalDetailsByName(mlHerbName).enqueue(object : Callback<List<MLDetailsResponse>> {
            override fun onResponse(
                call: Call<List<MLDetailsResponse>>,
                response: Response<List<MLDetailsResponse>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    val item = data.find { it.mlherbname == mlHerbName }
                    if (item != null) {
                        binding.herbTitle.text = item.mlherbname
                        binding.HerbTitle.text = item.mlherbname
                        binding.recommend.text = item.recommended
                        binding.risky.text = item.risky
                        Glide.with(requireContext())
                            .load(item.mlherbimageurl)
                            .into(binding.herbImage)
                        binding.herbDescription.text = item.mlherbdescription
                    } else {
                        Toast.makeText(context, "No data available for this herb", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<MLDetailsResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchHerbalBenefits(mlHerbName: String) {
        RetrofitClient.instance.getHerbalBenefitsByName(mlHerbName).enqueue(object : Callback<List<MLBenefitsResponse>> {
            override fun onResponse(
                call: Call<List<MLBenefitsResponse>>,
                response: Response<List<MLBenefitsResponse>>
            ) {
                if (response.isSuccessful) {
                    val benefits = response.body() ?: emptyList()
                    val filteredBenefits = benefits.filter { it.mlherbname == mlHerbName }
                    if (filteredBenefits.isNotEmpty()) {
                        benefitAdapter = MLHerbalBenifitAdapter(filteredBenefits)
                        binding.benifitHerbRecycleView.adapter = benefitAdapter
                    } else {
                        Log.d("HerbalBenefits", "No benefits available")
                    }
                } else {
                    Log.e("HerbalBenefits", "Response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MLBenefitsResponse>>, t: Throwable) {
                Log.e("HerbalBenefits", "Fetch failed: ${t.message}")
            }
        })
    }

    private fun fetchHerbalSteps(mlHerbName: String) {
        RetrofitClient.instance.getHerbalStepsByName(mlHerbName).enqueue(object : Callback<List<MLStepsResponse>> {
            override fun onResponse(
                call: Call<List<MLStepsResponse>>,
                response: Response<List<MLStepsResponse>>
            ) {
                if (response.isSuccessful) {
                    val steps = response.body() ?: emptyList()
                    val filteredSteps = steps.filter { it.mlherbname == mlHerbName }
                    if (filteredSteps.isNotEmpty()) {
                        stepsAdapter = MLHerbalStepsAdapter(filteredSteps)
                        binding.stepCoreRecycleView.layoutManager = LinearLayoutManager(context)
                        binding.stepCoreRecycleView.adapter = stepsAdapter
                    } else {
                        Log.d("HerbalSteps", "No steps available")
                    }
                } else {
                    Log.e("HerbalSteps", "Response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<MLStepsResponse>>, t: Throwable) {
                Log.e("HerbalSteps", "Fetch failed: ${t.message}")
            }
        })
    }

    private fun insert(userHistory: UserHistory) {
        RetrofitClient.instance.insertUserHistory(userHistory)
            .enqueue(object : Callback<UserHistory> {
                override fun onResponse(call: Call<UserHistory>, response: Response<UserHistory>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {

                        }
                    } else {
                        Toast.makeText(context, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserHistory>, t: Throwable) {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}