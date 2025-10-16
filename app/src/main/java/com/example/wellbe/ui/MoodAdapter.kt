package com.example.wellbe.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellbe.R
import com.example.wellbe.models.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(private val items: List<MoodEntry>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class MoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val emoji: TextView = view.findViewById(R.id.tvEmoji)
        private val label: TextView = view.findViewById(R.id.tvLabel)
        private val note: TextView = view.findViewById(R.id.tvNote)
        private val time: TextView = view.findViewById(R.id.tvTime)

        fun bind(entry: MoodEntry) {
            // ✅ use entry.mood instead of entry.emoji
            emoji.text = entry.mood

            // ✅ set label based on emoji
            label.text = when (entry.mood) {
                "😡" -> "Angry"
                "😢" -> "Sad"
                "🙂" -> "Okay"
                "😍" -> "Love"
                "😊" -> "Happy"
                else -> "Mood"
            }

            note.text = entry.note ?: ""

            // If you store timestamp, format it; else use entry.date
            val sdf = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault())
            time.text = entry.date
        }
    }
}
