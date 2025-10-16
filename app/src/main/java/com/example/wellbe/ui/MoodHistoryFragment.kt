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

class MoodHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerMoodHistory)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val prefs = Prefs(requireContext())
        val moodList = prefs.loadMoodEntries()

        recycler.adapter = MoodAdapter(moodList)
    }
}
