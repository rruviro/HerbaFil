package com.application.herbafill.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.herbafill.Model.MLBenefitsResponse
import com.application.herbafill.Model.MLDetailsResponse
import com.application.herbafill.Model.MLStepsResponse
import com.application.herbafill.R
import com.bumptech.glide.Glide

class MLHerbalBenifitAdapter(private val benefits: List<MLBenefitsResponse>) :
    RecyclerView.Adapter<MLHerbalBenifitAdapter.HerbalBenefitViewHolder>() {

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
        holder.benefitDescription.text = benefit.mlbenefitdescription

        Glide.with(holder.itemView.context)
            .load(benefit.mlbenefitimageUrl)
            .into(holder.benefitImage)
    }

    override fun getItemCount(): Int = benefits.size
}

class MLHerbalStepsAdapter(private val steps: List<MLStepsResponse>) :
    RecyclerView.Adapter<MLHerbalStepsAdapter.HerbalStepsViewHolder>() {

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
        holder.stepDescription.text = step.mlsteptitle

        val bulletPoints = step.mlstepdetails.split(",") // Adjust if needed
        val bulletAdapter = MLHerbalBulletAdapter(bulletPoints)
        holder.bulletRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.bulletRecyclerView.adapter = bulletAdapter
    }

    override fun getItemCount(): Int = steps.size
}

class MLHerbalBulletAdapter(private val bulletPoints: List<String>) :
    RecyclerView.Adapter<MLHerbalBulletAdapter.BulletViewHolder>() {

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

    override fun getItemCount(): Int = bulletPoints.size
}
