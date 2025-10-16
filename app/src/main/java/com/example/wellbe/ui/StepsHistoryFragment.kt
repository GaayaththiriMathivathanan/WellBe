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

class StepsHistoryFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var prefs: Prefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_steps_history, container, false)

        barChart = view.findViewById(R.id.barChartSteps)
        prefs = Prefs(requireContext())
        loadData()
        return view
    }

    private fun loadData() {
        val last7 = prefs.getLast7DaysSteps()
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()
        var i = 0f

        for ((date, steps) in last7) {
            entries.add(BarEntry(i, steps.toFloat()))
            labels.add(date.substring(5)) // show MM-DD
            i++
        }

        val dataSet = BarDataSet(entries, "Steps")
        dataSet.color = Color.BLUE

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.axisRight.isEnabled = false
        barChart.invalidate()
    }
}
