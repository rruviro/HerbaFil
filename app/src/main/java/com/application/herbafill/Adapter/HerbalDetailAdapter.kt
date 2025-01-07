package com.application.herbafill.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.herbafill.Model.HerbalBenefitsResponse
import com.application.herbafill.Model.HerbalDetailResponse
import com.application.herbafill.Model.HerbalStepsResponse
import com.application.herbafill.R
import com.bumptech.glide.Glide

class HerbalBenifitAdapter(private val benefits: List<HerbalBenefitsResponse>) :
    RecyclerView.Adapter<HerbalBenifitAdapter.HerbalBenefitViewHolder>() {

    class HerbalBenefitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val benefitImage: ImageView = view.findViewById(R.id.herbsBenifitImage)
        val benefitDescription: TextView = view.findViewById(R.id.herbsBenifitDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbalBenefitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.horizontalherbdets, parent, false)
        return HerbalBenefitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HerbalBenefitViewHolder, position: Int) {
        val benefit = benefits[position]
        holder.benefitDescription.text = benefit.benefitdescription
        Glide.with(holder.itemView.context)
            .load(benefit.benefitimageurl)
            .into(holder.benefitImage)
    }

    override fun getItemCount(): Int {
        return benefits.size
    }
}

class HerbalStepsAdapter(private val steps: List<HerbalStepsResponse>) :
    RecyclerView.Adapter<HerbalStepsAdapter.HerbalStepsViewHolder>() {

    class HerbalStepsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepDescription: TextView = view.findViewById(R.id.StepTitle)
        val bulletRecyclerView: RecyclerView = view.findViewById(R.id.childBulletText_Recycle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbalStepsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.herbdetstext, parent, false)
        return HerbalStepsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HerbalStepsViewHolder, position: Int) {
        val step = steps[position]
        holder.stepDescription.text = step.steptitle

        // Ensure step.stepDetails is of type List<String>
        val bulletAdapter = HerbalBulletAdapter(listOf(step.stepdetails))
        holder.bulletRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.bulletRecyclerView.adapter = bulletAdapter
    }

    override fun getItemCount(): Int {
        return steps.size
    }
}

class HerbalBulletAdapter(private val bulletPoints: List<String>) :
    RecyclerView.Adapter<HerbalBulletAdapter.BulletViewHolder>() {

    class BulletViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bulletText: TextView = view.findViewById(R.id.childBulletText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.herbdetsbullet, parent, false)
        return BulletViewHolder(view)
    }

    override fun onBindViewHolder(holder: BulletViewHolder, position: Int) {
        holder.bulletText.text = bulletPoints[position]
    }

    override fun getItemCount(): Int {
        return bulletPoints.size
    }

}

