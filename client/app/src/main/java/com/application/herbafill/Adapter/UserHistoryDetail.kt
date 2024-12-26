package com.application.herbafill.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.herbafill.Model.UserHistoryDetail
import com.application.herbafill.R
import com.bumptech.glide.Glide

class UserHistoryAdapter(
    private var historyList: List<UserHistoryDetail>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<UserHistoryAdapter.UserHistoryViewHolder>(), Filterable {

    private var filteredHistoryList: List<UserHistoryDetail> = historyList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.herbalitems, parent, false)
        return UserHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserHistoryViewHolder, position: Int) {
        val history = filteredHistoryList[position]
        holder.titleText.text = history.mlHerbName
        holder.descriptionText.text = history.mlLimitedDescript

        Glide.with(holder.itemView.context)
            .load(history.mlHerbImageUrl)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            listener.onItemClick(history)
        }
    }

    override fun getItemCount(): Int = filteredHistoryList.size

    interface OnItemClickListener {
        fun onItemClick(history: UserHistoryDetail)
    }

    inner class UserHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.title)
        val descriptionText: TextView = view.findViewById(R.id.description)
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim() ?: ""

                filteredHistoryList = if (query.isEmpty()) {
                    historyList
                } else {
                    historyList.filter { history ->
                        history.mlHerbName.contains(query, ignoreCase = true)
                    }
                }

                return FilterResults().apply {
                    values = filteredHistoryList
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredHistoryList = results?.values as? List<UserHistoryDetail> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }
}