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

class CyclingHistoryFragment : Fragment() {

    private lateinit var prefs: Prefs
    private lateinit var recyclerView: RecyclerView
    private lateinit var chart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cycling_history, container, false)

        prefs = Prefs(requireContext())
        recyclerView = view.findViewById(R.id.recyclerCyclingHistory)
        chart = view.findViewById(R.id.cyclingChart)

        val rides = prefs.loadCycling()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CyclingAdapter(rides)

        setupChart(rides)

        return view
    }

    private fun setupChart(rides: List<com.example.wellbe.models.CycleEntry>) {
        if (rides.isEmpty()) {
            chart.clear()
            return
        }

        val entries = rides.takeLast(7).mapIndexed { index, ride ->
            val dist = ride.distance.replace(" km", "").toFloatOrNull() ?: 0f
            BarEntry(index.toFloat(), dist)
        }

        val dataSet = BarDataSet(entries, "Cycling Distance (km)")
        dataSet.setColors(intArrayOf(R.color.teal_700, R.color.purple_500), requireContext())

        val data = BarData(dataSet)
        chart.data = data
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.invalidate()
    }
}
