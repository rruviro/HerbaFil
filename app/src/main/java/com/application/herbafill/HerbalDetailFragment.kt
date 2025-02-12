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
import com.application.herbafill.Adapter.HerbalBenifitAdapter
import com.application.herbafill.Adapter.HerbalStepsAdapter
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.HerbalBenefitsResponse
import com.application.herbafill.Model.HerbalDetailResponse
import com.application.herbafill.Model.HerbalStepsResponse
import com.application.herbafill.databinding.FragmentHerbalDetailBinding
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HerbalDetailFragment : Fragment() {

    private lateinit var binding: FragmentHerbalDetailBinding
    private lateinit var benefitAdapter: HerbalBenifitAdapter
    private lateinit var stepsAdapter: HerbalStepsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHerbalDetailBinding.inflate(inflater, container, false)

        val herbId = arguments?.getInt("herbId") ?: return binding.root
        fetchHerbalBenefits(herbId)
        fetchHerbalSteps(herbId)
        fetchAndDisplayHerbalData(herbId)

        val userID = arguments?.getInt("userID") ?: return binding.root
        val email = arguments?.getString("email") ?: return binding.root
        val bundle = Bundle().apply {
            putInt("userID", userID)
            putString("email", email)
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_herbalDetailFragment_to_homeFragment, bundle)
        }

        return binding.root
    }

    private fun fetchAndDisplayHerbalData(herbId: Int) {
        RetrofitClient.instance.getHerbalDetails(herbId).enqueue(object : Callback<List<HerbalDetailResponse>> {
            override fun onResponse(
                call: Call<List<HerbalDetailResponse>>,
                response: Response<List<HerbalDetailResponse>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body() ?: emptyList()
                    val item = data.find { it.herbid == herbId }

                    if (item != null) {
                        binding.herbTitle.text = item.herbname
                        binding.HerbTitle.text = item.herbname
                        binding.recommend.text = item.recommended
                        binding.risky.text = item.risky
                        Glide.with(requireContext())
                            .load(item.herbimage)
                            .into(binding.herbImage)
                        binding.herbDescription.text = item.herbdescription
                    } else {
                        Toast.makeText(context, "No data available for this herbId", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<HerbalDetailResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchHerbalBenefits(herbId: Int) {
        RetrofitClient.instance.getHerbalBenefits(herbId).enqueue(object : Callback<List<HerbalBenefitsResponse>> {
            override fun onResponse(
                call: Call<List<HerbalBenefitsResponse>>,
                response: Response<List<HerbalBenefitsResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { benefits ->
                        Log.d("FETCH_BENEFITS", "Received benefits: $benefits")
                        val filteredBenefits = benefits.filter { it.herbid == herbId }
                        Log.d("FETCH_BENEFITS", "Filtered benefits: $filteredBenefits")
                        benefitAdapter = HerbalBenifitAdapter(filteredBenefits)
                        binding.benifitHerbRecycleView.adapter = benefitAdapter
                    }
                }
            }

            override fun onFailure(call: Call<List<HerbalBenefitsResponse>>, t: Throwable) {
            }
        })
    }

    private fun fetchHerbalSteps(herbId: Int) {
        RetrofitClient.instance.getHerbalSteps(herbId).enqueue(object : Callback<List<HerbalStepsResponse>> {
            override fun onResponse(
                call: Call<List<HerbalStepsResponse>>,
                response: Response<List<HerbalStepsResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { steps ->
                        val filteredSteps = steps.filter { it.herbid == herbId }

                        Log.d("HerbalSteps", "Fetched steps: $filteredSteps")

                        if (filteredSteps.isEmpty()) {
                            Log.d("HerbalSteps", "No steps available")
                        } else {
                            stepsAdapter = HerbalStepsAdapter(filteredSteps)
                            binding.stepCoreRecycleView.layoutManager = LinearLayoutManager(context)
                            binding.stepCoreRecycleView.adapter = stepsAdapter
                            stepsAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.e("HerbalSteps", "Response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<HerbalStepsResponse>>, t: Throwable) {
                Log.e("HerbalSteps", "Fetch failed: ${t.message}")
            }
        })
    }

}

