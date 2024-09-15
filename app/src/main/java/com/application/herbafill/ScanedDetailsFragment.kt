package com.application.herbafill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.herbafill.Adapter.HerbalBenifitAdapter
import com.application.herbafill.Adapter.HerbalStepsAdapter
import com.application.herbafill.Adapter.MLHerbalBenifitAdapter
import com.application.herbafill.Adapter.MLHerbalStepsAdapter
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.HerbalStepsResponse
import com.application.herbafill.Model.MLBenefitsResponse
import com.application.herbafill.Model.MLDetailsResponse
import com.application.herbafill.Model.MLStepsResponse
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
        // Inflate the layout for this fragment
        binding = FragmentScanedDetailsBinding.inflate(inflater, container, false)
        val mlHerbName = arguments?.getString("mlHerbName") ?: return binding.root

        // Set the herb name in the UI
        binding.herbTitle.text = mlHerbName
        binding.HerbTitle.text = mlHerbName

        // Fetch data based on mlHerbName
        fetchAndDisplayHerbalData(mlHerbName)
        fetchHerbalBenefits(mlHerbName)
        fetchHerbalSteps(mlHerbName)

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
                    val item = data.find { it.mlHerbName == mlHerbName }
                    if (item != null) {
                        binding.herbTitle.text = item.mlHerbName
                        binding.HerbTitle.text = item.mlHerbName
                        Glide.with(requireContext())
                            .load(item.mlHerbImageUrl)
                            .into(binding.herbImage)
                        binding.herbDescription.text = item.mlHerbDescription
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
                    val filteredBenefits = benefits.filter { it.mlHerbName == mlHerbName }
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
                    val filteredSteps = steps.filter { it.mlHerbName == mlHerbName }
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
}