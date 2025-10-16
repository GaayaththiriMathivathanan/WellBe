package com.example.wellbe.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R
import com.example.wellbe.models.Habit

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onToggle: (Habit) -> Unit,
    private val onEdit: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit, onToggle, onEdit, onDelete)
    }

    override fun getItemCount(): Int = habits.size

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkboxHabit)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEditHabit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDeleteHabit)

        fun bind(
            habit: Habit,
            onToggle: (Habit) -> Unit,
            onEdit: (Habit) -> Unit,
            onDelete: (Habit) -> Unit
        ) {
            checkBox.text = habit.name
            checkBox.isChecked = habit.completedToday

            // strike-through if completed
            if (habit.completedToday) {
                checkBox.paintFlags = checkBox.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                checkBox.paintFlags =
                    checkBox.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            checkBox.setOnClickListener { onToggle(habit) }
            btnEdit.setOnClickListener { onEdit(habit) }
            btnDelete.setOnClickListener { onDelete(habit) }
        }
    }
}
