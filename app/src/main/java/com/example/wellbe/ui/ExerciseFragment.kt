package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R

data class Exercise(val name: String, val gifRes: Int, val benefits: String)

class ExerciseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerExercises)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val exercises = listOf(
            Exercise("Squat", R.drawable.squat, "Strengthens legs, glutes and core."),
            Exercise("Push-Up", R.drawable.pushup, "Builds chest, shoulders, triceps and core."),
            Exercise("Crunch", R.drawable.crunch, "Improves abdominal muscles and core stability."),
            Exercise("Plank", R.drawable.plank, "Boosts core strength and endurance."),
            Exercise("Lunge", R.drawable.lunge, "Improves balance, legs, and glutes."),
            Exercise("Skipping (Aerobics)", R.drawable.skipping, "Burns calories, improves cardio health.")
        )

        recycler.adapter = ExerciseAdapter(exercises)
    }
}
