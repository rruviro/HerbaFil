package com.application.herbafill

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.herbafill.Adapter.HerbalAdapter
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Herbals
import com.application.herbafill.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(), HerbalAdapter.OnItemClickListener {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var herbalAdapter: HerbalAdapter
    private var herbalList: List<Herbals> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.herbalRecycleView.layoutManager = LinearLayoutManager(context)
        setupRecyclerView()
        fetchHerbs()

        binding.searchTxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                herbalAdapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val userID = arguments?.getInt("userID") ?: return binding.root
        val email = arguments?.getString("email") ?: return binding.root
        val bundle = Bundle().apply {
            putInt("userID", userID)
            putString("email", email)
        }

        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment, bundle)
        }

        return binding.root
    }

    private fun fetchHerbs() {
        RetrofitClient.instance.getHerbs().enqueue(object : Callback<List<Herbals>> {
            override fun onResponse(call: Call<List<Herbals>>, response: Response<List<Herbals>>) {
                if (response.isSuccessful) {
                    val herbalList = response.body() ?: emptyList()
                    herbalAdapter = HerbalAdapter(herbalList, this@SearchFragment)
                    binding.herbalRecycleView.adapter = herbalAdapter
                } else {
                    Toast.makeText(context, "Failed to fetch herbs", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Herbals>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        binding.herbalRecycleView.layoutManager = LinearLayoutManager(requireContext())
        herbalAdapter = HerbalAdapter(herbalList, this)
        binding.herbalRecycleView.adapter = herbalAdapter
    }

    override fun onItemClick(herbals: Herbals) {
        val userID = arguments?.getInt("userID") ?: run {
            Toast.makeText(requireContext(), "User ID is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val email = arguments?.getString("email") ?: run {
            Toast.makeText(requireContext(), "User email is missing", Toast.LENGTH_SHORT).show()
            return
        }
        val childBundle = Bundle()
        childBundle.putInt("herbId", herbals.herbID)
        childBundle.putInt("userID", userID)
        childBundle.putString("email", email)
        findNavController().navigate(R.id.action_searchFragment_to_herbalDetailFragment, childBundle)
    }

}