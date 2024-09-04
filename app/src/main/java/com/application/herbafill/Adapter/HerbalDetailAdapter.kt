package com.application.herbafill.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.herbafill.Model.HerbalBenifits
import com.application.herbafill.Model.HerbalSteps
import com.application.herbafill.R

class HerbalBenifitAdapter(private val herbalDetails: List<HerbalBenifits>) : RecyclerView.Adapter<HerbalBenifitAdapter.HerbalDetailViewHolder>() {

    class HerbalDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagebenifit: ImageView = view.findViewById(R.id.herbsBenifitImage)
        val descriptionBenifit: TextView = view.findViewById(R.id.herbsBenifitDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbalDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.horizontalherbdets, parent, false)
        return HerbalDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: HerbalDetailViewHolder, position: Int) {
        val herbals = herbalDetails[position]

        holder.imagebenifit.setImageResource(herbals.imagebenifit)
        holder.descriptionBenifit.text = herbals.descriptionBenifit
    }

    override fun getItemCount(): Int {
        return herbalDetails.size
    }

}

class HerbalStepsAdapter(private val herbalSteps: List<HerbalSteps>) : RecyclerView.Adapter<HerbalStepsAdapter.HerbalStepsViewHolder>() {
    class HerbalStepsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepTitle: TextView = view.findViewById(R.id.StepTitle)
        val bulletRecyclerView: RecyclerView = itemView.findViewById(R.id.childBulletText_Recycle)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbalStepsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.herbdetstext, parent, false)
        return HerbalStepsViewHolder(view)
    }
    override fun onBindViewHolder(holder: HerbalStepsViewHolder, position: Int) {
        val herbals = herbalSteps[position]
        holder.stepTitle.text = herbals.stepTitle

        val bulletAdapter = HerbalBulletAdapter(herbals.stepBullet)
        holder.bulletRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.bulletRecyclerView.adapter = bulletAdapter
    }
    override fun getItemCount(): Int {
        return herbalSteps.size
    }
}

class HerbalBulletAdapter(private val stepBullet: List<String>) : RecyclerView.Adapter<HerbalBulletAdapter.HerbalBulletViewHolder>() {
    class HerbalBulletViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stepBullet: TextView = view.findViewById(R.id.childBulletText)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbalBulletViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.herbdetsbullet, parent, false)
        return HerbalBulletViewHolder(view)
    }
    override fun onBindViewHolder(holder: HerbalBulletViewHolder, position: Int) {
        holder.stepBullet.text = stepBullet[position]
    }
    override fun getItemCount(): Int {
        return stepBullet.size
    }
}