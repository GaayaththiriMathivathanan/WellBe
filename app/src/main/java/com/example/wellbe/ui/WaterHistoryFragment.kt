package com.example.wellbe.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wellbe.R
import com.example.wellbe.storage.Prefs
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class WaterHistoryFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var prefs: Prefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_water_history, container, false)

        prefs = Prefs(requireContext())
        barChart = view.findViewById(R.id.barChart)

        loadChart()

        return view
    }

    private fun loadChart() {
        val history = prefs.getLast7DaysHistory()

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val today = Calendar.getInstance()

        for (i in 0 until 7) {
            val day = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val label = dateFormat.format(day.time)

            val value = history[SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(day.time)] ?: 0
            entries.add(BarEntry((6 - i).toFloat(), value.toFloat()))
            labels.add(label)
        }

        val dataSet = BarDataSet(entries, "Glasses of Water")
        dataSet.color = resources.getColor(R.color.teal_700, null)
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barChart.data = barData

        // X-axis
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels.reversed())
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.setDrawGridLines(false)

        // Y-axis
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false

        // Other settings
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }
}
