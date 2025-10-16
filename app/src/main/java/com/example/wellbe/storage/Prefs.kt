package com.example.wellbe.storage

import android.content.Context
import com.example.wellbe.models.Habit
import com.example.wellbe.models.MoodEntry
import com.example.wellbe.models.RunEntry
import com.example.wellbe.models.CycleEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class Prefs(context: Context) {
    private val prefs = context.getSharedPreferences("wellbe_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    //  Onboarding
    fun isFirstLaunch(): Boolean = prefs.getBoolean("first_launch", true)
    fun setFirstLaunch(first: Boolean) {
        prefs.edit().putBoolean("first_launch", first).apply()
    }

    // Reset everything at midnight
    fun resetDaily() {
        // Save yesterday’s steps into history
        saveTodaySteps(getTodaySteps())

        // Reset steps for new day
        setTodaySteps(0)
        setStepOffset(-1) // force re-initialization with next sensor value

        // Reset other trackers
        resetWaterCount()
        resetHabitsForToday()
    }

    // Habits
    fun loadHabits(): MutableList<Habit> {
        val json = prefs.getString("habits_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        val habits: MutableList<Habit> = gson.fromJson(json, type) ?: mutableListOf()

        val lastDate = prefs.getString("habits_last_date", "")
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastDate != today) {
            if (!lastDate.isNullOrBlank()) {
                saveHabitHistoryForDate(lastDate, habits)
            }

            habits.forEach { it.completedToday = false }
            saveHabits(habits)
            prefs.edit().putString("habits_last_date", today).apply()
        }
        return habits
    }

    fun saveHabits(list: List<Habit>) {
        val json = gson.toJson(list)
        prefs.edit().putString("habits_list", json).apply()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit().putString("habits_last_date", today).apply()
    }

    private fun resetHabitsForToday() {
        val habits = loadHabits()
        habits.forEach { it.completedToday = false }
        saveHabits(habits)
    }

    //  Habit History
    private fun getHabitHistoryMap(): MutableMap<String, List<Habit>> {
        val json = prefs.getString("habit_history", "{}")
        val type = object : TypeToken<MutableMap<String, List<Habit>>>() {}.type
        return gson.fromJson(json, type) ?: mutableMapOf()
    }

    private fun saveHabitHistoryMap(map: Map<String, List<Habit>>) {
        prefs.edit().putString("habit_history", gson.toJson(map)).apply()
    }

    fun saveHabitHistoryForDate(date: String, habits: List<Habit>) {
        if (date.isBlank()) return
        val history = getHabitHistoryMap()
        val completed = habits.filter { it.completedToday }
        if (completed.isNotEmpty()) {
            history[date] = completed.map { it.copy() }
            saveHabitHistoryMap(history)
        }
    }

    fun loadHabitHistory(): List<Pair<String, List<Habit>>> {
        val history = getHabitHistoryMap()
        return history.entries.map { it.key to it.value }.sortedByDescending { it.first }
    }

    //  Completion
    fun saveTodayCompletionPercent() {
        val percent = getTodayCompletionPercent()
        val history = getHabitCompletionHistory()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        history[today] = percent
        prefs.edit().putString("habit_completion_history", gson.toJson(history)).apply()
    }

    fun getHabitCompletionHistory(): MutableMap<String, Int> {
        val json = prefs.getString("habit_completion_history", "{}")
        val type = object : TypeToken<MutableMap<String, Int>>() {}.type
        return gson.fromJson(json, type) ?: mutableMapOf()
    }

    fun getCompletionPercentForDate(date: String): Int {
        val history = getHabitCompletionHistory()
        return history[date] ?: 0
    }

    fun getLast7DaysHabitCompletion(): Map<String, Int> {
        val history = getHabitCompletionHistory()
        val cal = Calendar.getInstance()
        val last7 = mutableMapOf<String, Int>()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0..6) {
            val date = format.format(cal.time)
            last7[date] = history[date] ?: 0
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        return last7.toSortedMap()
    }

    //  Mood entries
    fun loadMoodEntries(): MutableList<MoodEntry> {
        val json = prefs.getString("mood_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveMoodEntries(list: List<MoodEntry>) {
        val json = gson.toJson(list)
        prefs.edit().putString("mood_list", json).apply()
    }

    //  Runs
    fun loadRuns(): MutableList<RunEntry> {
        val json = prefs.getString("run_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<RunEntry>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveRuns(list: List<RunEntry>) {
        val json = gson.toJson(list)
        prefs.edit().putString("run_list", json).apply()
    }

    //  Cycling
    fun loadCycling(): MutableList<CycleEntry> {
        val json = prefs.getString("cycling_list", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<CycleEntry>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun saveCycling(list: List<CycleEntry>) {
        val json = gson.toJson(list)
        prefs.edit().putString("cycling_list", json).apply()
    }

    // Hydration
    fun getHydrationIntervalMinutes(): Int = prefs.getInt("hydration_interval_min", 60)
    fun setHydrationIntervalMinutes(mins: Int) {
        prefs.edit().putInt("hydration_interval_min", mins).apply()
    }

    //  Steps
    fun getTodaySteps(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val lastDate = prefs.getString("steps_last_date", "")

        return if (lastDate != today) {
            // Save yesterday before reset
            if (!lastDate.isNullOrBlank()) {
                saveStepsForDate(lastDate, prefs.getInt("today_steps", 0))
            }

            prefs.edit()
                .putInt("today_steps", 0)
                .putInt("step_offset", -1)
                .putString("steps_last_date", today)
                .apply()
            0
        } else {
            prefs.getInt("today_steps", 0)
        }
    }

    fun setTodaySteps(steps: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        prefs.edit()
            .putInt("today_steps", steps.coerceAtLeast(0))
            .putString("steps_last_date", today)
            .apply()
    }

    fun saveTodaySteps(steps: Int) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val history = getStepHistory()
        history[today] = steps.coerceAtLeast(0)
        prefs.edit()
            .putString("step_history", gson.toJson(history))
            .putInt("today_steps", steps.coerceAtLeast(0))
            .putString("steps_last_date", today)
            .apply()
    }

    private fun saveStepsForDate(date: String, steps: Int) {
        if (date.isBlank()) return
        val history = getStepHistory()
        history[date] = steps.coerceAtLeast(0)
        prefs.edit().putString("step_history", gson.toJson(history)).apply()
    }

    fun getStepOffset(): Int = prefs.getInt("step_offset", -1)
    fun setStepOffset(offset: Int) { prefs.edit().putInt("step_offset", offset).apply() }

    fun getStepHistory(): MutableMap<String, Int> {
        val json = prefs.getString("step_history", "{}")
        val type = object : TypeToken<MutableMap<String, Int>>() {}.type
        return gson.fromJson(json, type) ?: mutableMapOf()
    }

    fun getLast7DaysSteps(): Map<String, Int> {
        val history = getStepHistory()
        val cal = Calendar.getInstance()
        val last7 = mutableMapOf<String, Int>()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0..6) {
            val date = format.format(cal.time)
            last7[date] = history[date] ?: 0
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        return last7.toSortedMap()
    }

    //  Water Tracker
    fun getWaterCount(): Int = prefs.getInt("water_count", 0)
    fun setWaterCount(count: Int) { prefs.edit().putInt("water_count", count).apply() }
    fun resetWaterCount() { prefs.edit().putInt("water_count", 0).apply() }

    fun getWaterGoal(): Int = prefs.getInt("water_goal", 8)
    fun setWaterGoal(goal: Int) { prefs.edit().putInt("water_goal", goal).apply() }

    //  Water History
    fun getWaterHistory(): MutableMap<String, Int> {
        val json = prefs.getString("water_history", "{}")
        val type = object : TypeToken<MutableMap<String, Int>>() {}.type
        return gson.fromJson(json, type) ?: mutableMapOf()
    }

    fun setWaterCountForToday(count: Int) {
        val history = getWaterHistory()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        history[today] = count
        prefs.edit().putString("water_history", gson.toJson(history)).apply()
        setWaterCount(count)
    }

    fun getLast7DaysHistory(): Map<String, Int> {
        val history = getWaterHistory()
        val cal = Calendar.getInstance()
        val last7 = mutableMapOf<String, Int>()
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0..6) {
            val date = format.format(cal.time)
            last7[date] = history[date] ?: 0
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        return last7.toSortedMap()
    }

    // Completion
    fun getTodayCompletionPercent(): Int {
        val habits = loadHabits()
        if (habits.isEmpty()) return 0
        val done = habits.count { it.completedToday }
        return (done * 100) / habits.size
    }

    //General helpers
    fun getInt(key: String, def: Int = 0): Int = prefs.getInt(key, def)
    fun setInt(key: String, value: Int) { prefs.edit().putInt(key, value).apply() }

    fun getBoolean(key: String, def: Boolean): Boolean = prefs.getBoolean(key, def)
    fun setBoolean(key: String, value: Boolean) { prefs.edit().putBoolean(key, value).apply() }

    fun getString(key: String, def: String = ""): String = prefs.getString(key, def) ?: def
    fun setString(key: String, value: String) { prefs.edit().putString(key, value).apply() }

    //  Step Goal
    fun getStepGoal(): Int = prefs.getInt("step_goal", 5000)
    fun setStepGoal(goal: Int) { prefs.edit().putInt("step_goal", goal).apply() }
}
