package com.application.herbafill

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.herbafill.Adapter.HerbalAdapter
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Herbals
import com.application.herbafill.databinding.FragmentHomeBinding
import com.google.android.material.card.MaterialCardView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), HerbalAdapter.OnItemClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var herbalAdapter: HerbalAdapter
    private lateinit var herbalList: MutableList<Herbals>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.herbalRecycleView.layoutManager = LinearLayoutManager(this.context)

        fetchHerbs()

        binding.menu.setOnClickListener {
            if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        val headerView = binding.navigationView.getHeaderView(0)
        val profileBtn = headerView.findViewById<MaterialCardView>(R.id.profileBtn)
        val backBtn = headerView.findViewById<TextView>(R.id.backBtn)
        val historyBtn = headerView.findViewById<MaterialCardView>(R.id.historyBtn)
        val logoutBtn = headerView.findViewById<MaterialCardView>(R.id.logoutBtn)

        backBtn.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        profileBtn.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        historyBtn.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        binding.scanBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scanITFragment)
        }

        logoutBtn.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            showImageOptions()
        }

        binding.searchBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        return binding.root
    }

    private fun fetchHerbs() {
        RetrofitClient.instance.getHerbs().enqueue(object : Callback<List<Herbals>> {
            override fun onResponse(call: Call<List<Herbals>>, response: Response<List<Herbals>>) {
                if (response.isSuccessful) {
                    val herbalList = response.body() ?: emptyList()
                    herbalAdapter = HerbalAdapter(herbalList, this@HomeFragment)
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

    override fun onItemClick(herbals: Herbals) {
        val bundle = Bundle().apply {
            putInt("herbId", herbals.herbID)
        }
        findNavController().navigate(R.id.action_homeFragment_to_herbalDetailFragment, bundle)
    }

    private fun showImageOptions() {
        val dialogView = layoutInflater.inflate(R.layout.logout_dialog, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)

        val dialog = dialogBuilder.create()
        dialog.show()

        dialogView.findViewById<TextView>(R.id.logoutBtn).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
    }
}