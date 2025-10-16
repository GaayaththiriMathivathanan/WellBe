package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.wellbe.R
import com.example.wellbe.models.MoodEntry
import com.example.wellbe.storage.Prefs
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {

    private lateinit var prefs: Prefs
    private lateinit var textHistory: TextView
    private lateinit var chart: BarChart
    private var selectedEmoji: String? = null
    private var selectedLabel: String? = null

    // ✅ map emoji -> numeric value (for chart)
    private val moodMap = mapOf(
        "😡" to 0f,
        "😢" to 1f,
        "🙂" to 2f,
        "😍" to 3f,
        "😊" to 4f
    )

    // ✅ emoji -> label mapping
    private val moodLabels = mapOf(
        "😡" to "Angry",
        "😢" to "Sad",
        "🙂" to "Okay",
        "😍" to "Love",
        "😊" to "Happy"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = Prefs(requireContext())
        textHistory = view.findViewById(R.id.textHistory)
        chart = view.findViewById(R.id.moodChart)

        // map buttons to moods
        val moods = mapOf(
            R.id.moodHappy to "😊",
            R.id.moodSad to "😢",
            R.id.moodAngry to "😡",
            R.id.moodLove to "😍"
        )

        // select mood
        moods.forEach { (id, emoji) ->
            view.findViewById<TextView>(id).setOnClickListener {
                selectedEmoji = emoji
                selectedLabel = moodLabels[emoji]
                Toast.makeText(requireContext(), "Selected mood: $emoji $selectedLabel", Toast.LENGTH_SHORT).show()
            }
        }

        // save mood
        view.findViewById<Button>(R.id.btnSaveMood).setOnClickListener {
            if (selectedEmoji == null) {
                Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // ✅ store emoji + label
            val moodEntry = MoodEntry(
                date = today,
                mood = selectedEmoji!!,
                label = selectedLabel ?: ""
            )

            val list = prefs.loadMoodEntries()
            list.add(moodEntry)
            prefs.saveMoodEntries(list)

            updateHistory(list)
            updateChart(list)
        }

        // history page button
        view.findViewById<Button>(R.id.btnViewHistory).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MoodHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        val savedList = prefs.loadMoodEntries()
        updateHistory(savedList)
        updateChart(savedList)
    }

    private fun updateHistory(list: MutableList<MoodEntry>) {
        if (list.isEmpty()) {
            textHistory.text = "No moods logged yet"
        } else {
            val historyText = list.takeLast(5).joinToString("\n") {
                "${it.date}: ${it.mood} (${it.label})"
            }
            textHistory.text = historyText
        }
    }

    private fun updateChart(list: MutableList<MoodEntry>) {
        if (list.isEmpty()) {
            chart.clear()
            return
        }

        val entries = list.takeLast(7).mapIndexed { index, moodEntry ->
            BarEntry(index.toFloat(), moodMap[moodEntry.mood] ?: 0f)
        }

        val dataSet = BarDataSet(entries, "Mood over time")
        dataSet.setColors(
            intArrayOf(R.color.teal_700, R.color.purple_500, R.color.primaryColor),
            requireContext()
        )

        val data = BarData(dataSet)
        chart.data = data
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.invalidate()
    }
}
