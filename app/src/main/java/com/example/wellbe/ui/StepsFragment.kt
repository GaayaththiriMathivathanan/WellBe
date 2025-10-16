package com.example.wellbe.ui

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.wellbe.R
import com.example.wellbe.storage.Prefs

class StepsFragment : Fragment(), SensorEventListener {

    private lateinit var prefs: Prefs
    private lateinit var textStepCount: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnHistory: Button

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_steps, container, false)

        prefs = Prefs(requireContext())
        textStepCount = view.findViewById(R.id.textStepCount)
        progressBar = view.findViewById(R.id.progressBarSteps)
        btnHistory = view.findViewById(R.id.btnStepHistory)

        val stepGoal = prefs.getStepGoal()
        progressBar.max = stepGoal

        sensorManager =
            requireActivity().getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Request permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                1002
            )
        }

        //Initialize UI safely
        updateUI(prefs.getTodaySteps())

        btnHistory.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragmentContainer, StepsHistoryFragment())
                addToBackStack(null)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        } ?: run {
            textStepCount.text = "Step sensor not available"
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceBoot = event.values[0].toInt()

            //  Initialize offset only once
            if (prefs.getStepOffset() == -1) {
                prefs.setStepOffset(totalStepsSinceBoot)
            }

            var stepsToday = totalStepsSinceBoot - prefs.getStepOffset()

            // Safety: prevent negative values
            if (stepsToday < 0) {
                stepsToday = 0
                prefs.setStepOffset(totalStepsSinceBoot)
            }

            prefs.saveTodaySteps(stepsToday)
            updateUI(stepsToday)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun updateUI(steps: Int) {
        val stepGoal = prefs.getStepGoal()
        textStepCount.text = "$steps / $stepGoal steps"
        progressBar.max = stepGoal
        progressBar.progress = steps.coerceAtMost(stepGoal)
    }
}
