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

class HabitHistoryFragment : Fragment() {

    private lateinit var prefs: Prefs
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habit_history, container, false)

        prefs = Prefs(requireContext())
        recyclerView = view.findViewById(R.id.recyclerHabitHistory)

        val history = prefs.loadHabitHistory()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = HabitHistoryAdapter(history)

        return view
    }
}
