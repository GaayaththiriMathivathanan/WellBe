package com.example.wellbe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R
import com.example.wellbe.models.CycleEntry

class CyclingAdapter(private val rides: List<CycleEntry>) :
    RecyclerView.Adapter<CyclingAdapter.CycleViewHolder>() {

    class CycleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textCycleStats: TextView = itemView.findViewById(R.id.textCycleStats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CycleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cycling, parent, false)
        return CycleViewHolder(view)
    }

    override fun onBindViewHolder(holder: CycleViewHolder, position: Int) {
        val ride = rides[position]
        holder.textCycleStats.text =
            "${ride.date}\nDuration: ${ride.duration}\nDistance: ${ride.distance}\nCalories: ${ride.calories}"
    }

    override fun getItemCount(): Int = rides.size
}
