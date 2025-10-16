package com.example.wellbe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R
import com.example.wellbe.models.Habit

class HabitHistoryAdapter(
    private val history: List<Pair<String, List<Habit>>>
) : RecyclerView.Adapter<HabitHistoryAdapter.HistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val (date, habits) = history[position]

        holder.textDate.text = date

        // Build a string of habits with checkmarks
        val builder = StringBuilder()
        for (habit in habits) {
            if (habit.completedToday) {
                builder.append("✓ ${habit.name}\n") // completed habit
            } else {
                builder.append("✗ ${habit.name}\n") // not completed
            }
        }

        holder.textHabits.text = builder.toString().trim()
    }

    override fun getItemCount(): Int = history.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textDate: TextView = itemView.findViewById(R.id.textHistoryDate)
        val textHabits: TextView = itemView.findViewById(R.id.textHistoryHabits)
    }
}
