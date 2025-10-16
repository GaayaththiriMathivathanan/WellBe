package com.example.wellbe.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.wellbe.R
import com.example.wellbe.storage.Prefs
import com.example.wellbe.utils.ReminderHelper

class SettingsFragment : Fragment() {

    private lateinit var prefs: Prefs
    private lateinit var txtProfile: TextView
    private lateinit var switchDarkMode: Switch
    private lateinit var switchHydrationReminder: Switch
    private lateinit var switchMoodReminder: Switch
    private lateinit var btnStepGoal: Button
    private lateinit var btnWaterGoal: Button
    private lateinit var btnEditProfile: Button
    private lateinit var btnClearHistory: Button
    private lateinit var btnHydrationInterval: Button
    private lateinit var btnMoodInterval: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        prefs = Prefs(requireContext())

        txtProfile = view.findViewById(R.id.txtProfile)
        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchHydrationReminder = view.findViewById(R.id.switchHydrationReminder)
        switchMoodReminder = view.findViewById(R.id.switchMoodReminder)
        btnStepGoal = view.findViewById(R.id.btnStepGoal)
        btnWaterGoal = view.findViewById(R.id.btnWaterGoal)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnClearHistory = view.findViewById(R.id.btnClearHistory)
        btnHydrationInterval = view.findViewById(R.id.btnHydrationInterval)
        btnMoodInterval = view.findViewById(R.id.btnMoodInterval)

        loadSettings()

        // Dark Mode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.setBoolean("dark_mode", isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Hydration Reminder
        switchHydrationReminder.setOnCheckedChangeListener { _, isChecked ->
            prefs.setBoolean("hydration_reminder", isChecked)
            val interval = prefs.getInt("hydration_interval", 30)
            if (isChecked) {
                ReminderHelper.scheduleReminder(
                    requireContext(),
                    interval,
                    1001,
                    "Time to drink water 💧"
                )
                Toast.makeText(requireContext(), "Hydration reminder ON ($interval min)", Toast.LENGTH_SHORT).show()
            } else {
                ReminderHelper.cancelReminder(requireContext(), 1001)
                Toast.makeText(requireContext(), "Hydration reminder OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // Mood Reminder
        switchMoodReminder.setOnCheckedChangeListener { _, isChecked ->
            prefs.setBoolean("mood_reminder", isChecked)
            val interval = prefs.getInt("mood_interval", 60)
            if (isChecked) {
                ReminderHelper.scheduleReminder(
                    requireContext(),
                    interval,
                    1002,
                    "How are you feeling? Log your mood 😊"
                )
                Toast.makeText(requireContext(), "Mood reminder ON ($interval min)", Toast.LENGTH_SHORT).show()
            } else {
                ReminderHelper.cancelReminder(requireContext(), 1002)
                Toast.makeText(requireContext(), "Mood reminder OFF", Toast.LENGTH_SHORT).show()
            }
        }

        // Step Goal
        btnStepGoal.setOnClickListener { showInputDialog("Step Goal", "Enter daily step goal", "step_goal") }

        // Water Goal
        btnWaterGoal.setOnClickListener { showInputDialog("Water Goal", "Enter daily water goal (glasses)", "water_goal") }

        // Hydration Interval
        btnHydrationInterval.setOnClickListener { showIntervalDialog("Hydration") }

        // Mood Interval
        btnMoodInterval.setOnClickListener { showIntervalDialog("Mood") }

        // Edit Profile
        btnEditProfile.setOnClickListener { showProfileDialog() }

        // Clear History
        btnClearHistory.setOnClickListener {
            prefs.saveHabits(emptyList())
            prefs.saveMoodEntries(emptyList())
            prefs.saveRuns(emptyList())
            prefs.saveCycling(emptyList())
            prefs.setTodaySteps(0)
            prefs.resetWaterCount()
            Toast.makeText(requireContext(), "All history cleared!", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun loadSettings() {
        val name = prefs.getString("profile_name", "Not set")
        val age = prefs.getInt("profile_age", 0)
        val weight = prefs.getInt("profile_weight", 0)

        txtProfile.text =
            "Name: $name\nAge: ${if (age > 0) age else "N/A"}\nWeight: ${if (weight > 0) weight else "N/A"}"

        switchDarkMode.isChecked = prefs.getBoolean("dark_mode", false)
        switchHydrationReminder.isChecked = prefs.getBoolean("hydration_reminder", false)
        switchMoodReminder.isChecked = prefs.getBoolean("mood_reminder", false)
    }

    private fun showInputDialog(title: String, message: String, key: String) {
        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val value = input.text.toString().toIntOrNull() ?: return@setPositiveButton
                prefs.setInt(key, value)
                Toast.makeText(requireContext(), "$title set to $value", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showIntervalDialog(type: String) {
        val key = "${type.lowercase()}_interval"
        val numberPicker = NumberPicker(requireContext()).apply {
            minValue = 1
            maxValue = 120
            value = prefs.getInt(key, 10)
            wrapSelectorWheel = true
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Set $type Reminder Interval (minutes)")
            .setView(numberPicker)
            .setPositiveButton("Save") { _, _ ->
                val interval = numberPicker.value
                prefs.setInt(key, interval)

                // Reschedule reminder immediately if switch is on
                if (type == "Hydration" && switchHydrationReminder.isChecked) {
                    ReminderHelper.scheduleReminder(requireContext(), interval, 1001, "Time to drink water 💧")
                }
                if (type == "Mood" && switchMoodReminder.isChecked) {
                    ReminderHelper.scheduleReminder(requireContext(), interval, 1002, "How are you feeling? Log your mood 😊")
                }

                Toast.makeText(requireContext(), "$type reminder set to $interval min", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_profile, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtName)
        val edtAge = dialogView.findViewById<EditText>(R.id.edtAge)
        val edtWeight = dialogView.findViewById<EditText>(R.id.edtWeight)

        edtName.setText(prefs.getString("profile_name", ""))
        edtAge.setText(prefs.getInt("profile_age", 0).takeIf { it > 0 }?.toString() ?: "")
        edtWeight.setText(prefs.getInt("profile_weight", 0).takeIf { it > 0 }?.toString() ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                prefs.setString("profile_name", edtName.text.toString())
                prefs.setInt("profile_age", edtAge.text.toString().toIntOrNull() ?: 0)
                prefs.setInt("profile_weight", edtWeight.text.toString().toIntOrNull() ?: 0)
                loadSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
