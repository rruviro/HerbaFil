package com.application.herbafill

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.herbafill.Adapter.HerbalAdapter
import com.application.herbafill.Api.RetrofitClient
import com.application.herbafill.Model.Account
import com.application.herbafill.Model.Herbals
import com.application.herbafill.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.navigation.NavigationView
import org.w3c.dom.Text
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

        val userID = arguments?.getInt("userID") ?: return binding.root
        val bundle = Bundle().apply {
            putInt("userID", userID)
        }

        fetchData(userID)
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
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
        }

        historyBtn.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment, bundle)
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

    private var userDetails: Account? = null
    private fun fetchData(userID: Int) {
        RetrofitClient.instance.getUserDetails(userID)
            .enqueue(object : Callback<Account> {
                override fun onResponse(call: Call<Account>, response: Response<Account>) {
                    if (response.isSuccessful) {
                        userDetails = response.body()
                        if (userDetails != null) {
                            val headerView = binding.navigationView.getHeaderView(0)
                            headerView?.findViewById<TextView>(R.id.name)?.text = userDetails?.name
                        } else {
                            Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Response not successful", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Account>, t: Throwable) {
                    Toast.makeText(context, "Connection Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
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