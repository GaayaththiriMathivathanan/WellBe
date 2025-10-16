package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.wellbe.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Find each card by ID
        val cardWalk = view.findViewById<CardView>(R.id.cardWalk)
        val cardRun = view.findViewById<CardView>(R.id.cardRun)
        val cardCycling = view.findViewById<CardView>(R.id.cardCycling)
        val cardWater = view.findViewById<CardView>(R.id.cardWater)
        val cardYoga = view.findViewById<CardView>(R.id.cardYoga)
        val cardExercise = view.findViewById<CardView>(R.id.cardExercise)

        // ✅ Handle clicks to open the correct fragment
        cardWalk.setOnClickListener { openFragment(StepsFragment()) }
        cardRun.setOnClickListener { openFragment(RunFragment()) }
        cardCycling.setOnClickListener { openFragment(CyclingFragment()) } // 🚴 cycling screen
        cardWater.setOnClickListener { openFragment(WaterTrackerFragment()) }
        cardYoga.setOnClickListener { openFragment(YogaFragment()) }
        cardExercise.setOnClickListener { openFragment(ExerciseFragment()) }
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null)
        }
    }
}
