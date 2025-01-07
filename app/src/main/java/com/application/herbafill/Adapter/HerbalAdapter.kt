package com.application.herbafill.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.herbafill.Model.Herbals
import com.application.herbafill.R
import com.bumptech.glide.Glide

class HerbalAdapter(
    private var herbalList: List<Herbals>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<HerbalAdapter.HerbalViewHolder>(), Filterable {

    private var filteredHerbalList: List<Herbals> = herbalList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HerbalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.herbalitems, parent, false)
        return HerbalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HerbalViewHolder, position: Int) {
        val herbals = filteredHerbalList[position]
        holder.titleText.text = herbals.herbName
        holder.descriptionText.text = herbals.herbDescrip

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(herbals.imageUrl)
            .into(holder.imageView)

        // Set click listener on the item view
        holder.itemView.setOnClickListener {
            listener.onItemClick(herbals)
        }
    }

    override fun getItemCount(): Int = filteredHerbalList.size

    interface OnItemClickListener {
        fun onItemClick(herbals: Herbals)
    }

    inner class HerbalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.title)
        val descriptionText: TextView = view.findViewById(R.id.description)
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim() ?: ""

                filteredHerbalList = if (query.isEmpty()) {
                    herbalList
                } else {
                    herbalList.filter { herbals ->
                        herbals.herbName.contains(query, ignoreCase = true)
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filteredHerbalList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredHerbalList = results?.values as List<Herbals>
                notifyDataSetChanged()
            }
        }
    }
}