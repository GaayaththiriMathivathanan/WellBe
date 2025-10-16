package com.example.wellbe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R

class YogaAdapter(private val items: List<Yoga>) :
    RecyclerView.Adapter<YogaAdapter.YogaViewHolder>() {

    class YogaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gif: ImageView = itemView.findViewById(R.id.yogaGif)
        val name: TextView = itemView.findViewById(R.id.yogaName)
        val benefits: TextView = itemView.findViewById(R.id.yogaBenefits)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YogaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_yoga, parent, false)
        return YogaViewHolder(view)
    }

    override fun onBindViewHolder(holder: YogaViewHolder, position: Int) {
        val yoga = items[position]
        holder.gif.setImageResource(yoga.gifRes)
        holder.name.text = yoga.name
        holder.benefits.text = yoga.benefits
    }

    override fun getItemCount(): Int = items.size
}
