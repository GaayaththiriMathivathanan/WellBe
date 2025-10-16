package com.example.wellbe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R

class ExerciseAdapter(private val items: List<Exercise>) :
    RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gif: ImageView = itemView.findViewById(R.id.exerciseGif)
        val name: TextView = itemView.findViewById(R.id.exerciseName)
        val benefits: TextView = itemView.findViewById(R.id.exerciseBenefits)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = items[position]
        holder.gif.setImageResource(exercise.gifRes)
        holder.name.text = exercise.name
        holder.benefits.text = exercise.benefits
    }

    override fun getItemCount(): Int = items.size
}
