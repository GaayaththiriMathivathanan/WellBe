package com.example.wellbe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R
import com.example.wellbe.models.RunEntry

class RunAdapter(private val runs: List<RunEntry>) :
    RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textRunStats: TextView = itemView.findViewById(R.id.textRunStats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_run, parent, false)
        return RunViewHolder(view)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = runs[position]
        holder.textRunStats.text =
            "${run.date}\nDuration: ${run.duration}\nDistance: ${run.distance}\nCalories: ${run.calories}"
    }

    override fun getItemCount(): Int = runs.size
}
