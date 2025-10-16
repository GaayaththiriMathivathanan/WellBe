package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R
import com.example.wellbe.models.Habit
import com.example.wellbe.storage.Prefs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class HabitFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddHabit: FloatingActionButton
    private lateinit var textDate: TextView
    private lateinit var textEmpty: TextView
    private lateinit var prefs: Prefs
    private lateinit var adapter: HabitAdapter
    private var habits = mutableListOf<Habit>()

    // ✅ New UI elements for progress
    private lateinit var progressBar: ProgressBar
    private lateinit var textProgress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habit, container, false)

        prefs = Prefs(requireContext())
        recyclerView = view.findViewById(R.id.recyclerHabits)
        fabAddHabit = view.findViewById(R.id.fabAddHabit)
        textDate = view.findViewById(R.id.textDate)
        textEmpty = view.findViewById(R.id.textEmpty)

        // ✅ Find new progress widgets
        progressBar = view.findViewById(R.id.progressHabits)
        textProgress = view.findViewById(R.id.textProgress)

        // Show today’s date
        val today = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        textDate.text = "Habits for $today"

        habits = prefs.loadHabits()

        adapter = HabitAdapter(
            habits,
            onToggle = { habit ->
                habit.completedToday = !habit.completedToday
                saveAndUpdate()  // ✅ save into prefs + history
            },
            onEdit = { habit -> showEditHabitDialog(habit) },
            onDelete = { habit ->
                habits.remove(habit)
                saveAndUpdate()  // ✅ save into prefs + history
                checkEmpty()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fabAddHabit.setOnClickListener { showAddHabitDialog() }

        checkEmpty()
        updateProgress() // initialize

        return view
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val inputName = dialogView.findViewById<TextInputEditText>(R.id.inputHabitName)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Habit")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = inputName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val habit = Habit(name, false)
                    habits.add(habit)
                    saveAndUpdate()  // ✅ save into prefs + history
                    checkEmpty()
                } else {
                    Toast.makeText(requireContext(), "Enter habit name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditHabitDialog(habit: Habit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val inputName = dialogView.findViewById<TextInputEditText>(R.id.inputHabitName)
        inputName.setText(habit.name)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = inputName.text.toString().trim()
                if (name.isNotEmpty()) {
                    habit.name = name
                    saveAndUpdate()  // ✅ save into prefs + history
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkEmpty() {
        if (habits.isEmpty()) {
            textEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    // ✅ Helper function: saves habits, updates history + progress
    private fun saveAndUpdate() {
        prefs.saveHabits(habits)
        prefs.saveTodayCompletionPercent()
        prefs.saveHabitHistoryForDate(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            habits
        )
        adapter.notifyDataSetChanged()
        updateProgress()
    }

    // ✅ Updates completion widget
    private fun updateProgress() {
        val percent = prefs.getTodayCompletionPercent()
        progressBar.progress = percent
        textProgress.text = "$percent%"
    }
}
