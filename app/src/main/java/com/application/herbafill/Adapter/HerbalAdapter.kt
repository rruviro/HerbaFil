package com.application.herbafill.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.herbafill.Model.Herbals
import com.application.herbafill.R

class HerbalAdapter(private var herbalList: List<Herbals>) : RecyclerView.Adapter<HerbalAdapter.HerbalViewHolder>() {

    class HerbalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.title)
        val descriptionText: TextView = view.findViewById(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.herbalitems, parent, false)
        return HerbalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HerbalViewHolder, position: Int) {
        val herbals = herbalList[position]
        holder.titleText.text = herbals.title
        holder.descriptionText.text = herbals.description
    }

    override fun getItemCount(): Int {
        return herbalList.size
    }

}