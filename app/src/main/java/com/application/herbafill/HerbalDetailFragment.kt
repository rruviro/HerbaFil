package com.application.herbafill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.herbafill.Adapter.HerbalBenifitAdapter
import com.application.herbafill.Adapter.HerbalStepsAdapter
import com.application.herbafill.Model.HerbalBenifits
import com.application.herbafill.Model.HerbalDetail
import com.application.herbafill.Model.HerbalSteps
import com.application.herbafill.databinding.FragmentHerbalDetailBinding

class HerbalDetailFragment : Fragment() {

    private lateinit var binding: FragmentHerbalDetailBinding
    private lateinit var herbalBenifitAdapter: HerbalBenifitAdapter
    private lateinit var herbalBenifits: MutableList<HerbalBenifits>

    private var dets = HerbalDetail(
        R.drawable.ginger,
        "Ginger",
        "Ginger is a flowering plant known for its rhizome, commonly referred to as ginger root or simply ginger, which is widely utilized as both a spice and in traditional medicine. It is a herbaceous perennial that grows annual pseudostems, which are false stems formed from the rolled bases of leaves, reaching about one meter in height and featuring narrow leaf blades. The plant produces inflorescences with flowers that have pale yellow petals with purple edges, emerging directly from the rhizome on separate shoots."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHerbalDetailBinding.inflate(inflater, container, false)

        binding.herbsTitle.text = dets.title
        binding.HerbsTitle.text = dets.title
        binding.herbsDescription.text = dets.description

        binding.benifitHerbRecycleView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        herbalBenifits = mutableListOf(
            HerbalBenifits(R.drawable.ginger, "May improve brain function and protect against Alzheimer’s disease."),
            HerbalBenifits(R.drawable.ginger, "May help reduce cancer risk."),
            HerbalBenifits(R.drawable.ginger, "May improve brain function and protect against Alzheimer’s disease.")
        )
        herbalBenifitAdapter = HerbalBenifitAdapter(herbalBenifits)
        binding.benifitHerbRecycleView.adapter = herbalBenifitAdapter

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        val steps = listOf(
            HerbalSteps("Preparation Step 1", listOf("Choose fresh ginger root that is firm, smooth, and free from mold.", "Peel the ginger before use.", "Cut or grate the ginger for different uses.")),
            HerbalSteps("Preparation Step 2", listOf("Store ginger in a cool, dry place.", "Use ginger within a week for best freshness."))
        )

        val adapter = HerbalStepsAdapter(steps)
        binding.stepCoreRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.stepCoreRecycleView.adapter = adapter
    }

}
