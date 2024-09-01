package com.application.herbafill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.herbafill.Adapter.HerbalAdapter
import com.application.herbafill.Model.Herbals
import com.application.herbafill.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var herbalAdapter: HerbalAdapter
    private lateinit var herbalList: MutableList<Herbals>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.herbalRecycleView.layoutManager = LinearLayoutManager(this.context)

        herbalList = mutableListOf(
            Herbals("Ginger", "Commonly used to relieve nausea, improve digestion, and reduce inflammation."),
            Herbals("Turmeric", "Known for its anti-inflammatory and antioxidant properties.")
        )
        herbalAdapter = HerbalAdapter(herbalList)

        binding.herbalRecycleView.adapter = herbalAdapter
        return binding.root
    }

}