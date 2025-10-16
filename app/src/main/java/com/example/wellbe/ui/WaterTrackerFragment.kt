package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wellbe.R
import com.example.wellbe.storage.Prefs
import java.text.SimpleDateFormat
import java.util.*

class WaterTrackerFragment : Fragment() {

    private lateinit var prefs: Prefs
    private lateinit var progressBar: ProgressBar
    private lateinit var textWaterCount: TextView
    private lateinit var btnAddWater: Button
    private lateinit var btnHistory: Button

    private var waterCount = 0
    private var dailyGoal = 8 // default 8 glasses

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_water_tracker, container, false)

        prefs = Prefs(requireContext())

        progressBar = view.findViewById(R.id.progressBarWater)
        textWaterCount = view.findViewById(R.id.textWaterCount)
        btnAddWater = view.findViewById(R.id.btnAddWater)
        btnHistory = view.findViewById(R.id.btnHistory)

        dailyGoal = prefs.getWaterGoal()

        // Load today's water count
        val today = dateFormat.format(Date())
        waterCount = prefs.getWaterHistory()[today] ?: 0
        updateUI()

        btnAddWater.setOnClickListener {
            if (waterCount < dailyGoal) {
                waterCount++
                prefs.setWaterCountForToday(waterCount)
                updateUI()
            }
        }

        btnHistory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, WaterHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun updateUI() {
        textWaterCount.text = "$waterCount / $dailyGoal glasses"
        progressBar.max = dailyGoal
        progressBar.progress = waterCount
    }
}
