package com.example.wellbe.models

data class MoodEntry(
    val date: String,      // e.g. "2025-10-01"
    val mood: String,      // e.g. "😊"
    val label: String? = null, // e.g. "Happy", optional
    val note: String? = null,  // optional user note
    val timestamp: Long = System.currentTimeMillis()
)
