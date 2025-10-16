package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R
import com.example.wellbe.storage.Prefs
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class RunHistoryFragment : Fragment() {

    private lateinit var prefs: Prefs
    private lateinit var recyclerView: RecyclerView
    private lateinit var chart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_run_history, container, false)

        prefs = Prefs(requireContext())
        recyclerView = view.findViewById(R.id.recyclerRunHistory)
        chart = view.findViewById(R.id.runChart)

        val runs = prefs.loadRuns()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RunAdapter(runs)

        setupChart(runs)

        return view
    }

    private fun setupChart(runs: List<com.example.wellbe.models.RunEntry>) {
        if (runs.isEmpty()) {
            chart.clear()
            return
        }

        // Distance chart
        val entries = runs.takeLast(7).mapIndexed { index, run ->
            val dist = run.distance.replace(" km", "").toFloatOrNull() ?: 0f
            BarEntry(index.toFloat(), dist)
        }

        val dataSet = BarDataSet(entries, "Distance (km)")
        dataSet.setColors(intArrayOf(R.color.purple_500, R.color.teal_700), requireContext())

        val barData = BarData(dataSet)
        chart.data = barData
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.invalidate()
    }
}
