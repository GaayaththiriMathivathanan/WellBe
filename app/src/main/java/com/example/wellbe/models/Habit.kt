package com.example.wellbe.models

data class Habit(
    var name: String,
    var completedToday: Boolean = false
)

data class HabitHistory(
    val date: String,
    val habits: List<Habit>
)
