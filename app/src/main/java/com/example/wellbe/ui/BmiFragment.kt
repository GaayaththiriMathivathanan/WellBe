package com.example.wellbe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.cardiomood.android.controls.gauge.SpeedometerGauge
import com.example.wellbe.R
import kotlin.math.pow

class BmiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bmi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputAge = view.findViewById<EditText>(R.id.inputAge)
        val inputHeight = view.findViewById<EditText>(R.id.inputHeight)
        val inputWeight = view.findViewById<EditText>(R.id.inputWeight)
        val btnCalculate = view.findViewById<Button>(R.id.btnCalculate)
        val textResult = view.findViewById<TextView>(R.id.textResult)
        val bmiGauge = view.findViewById<SpeedometerGauge>(R.id.bmiGauge)
        val btnViewChart = view.findViewById<Button>(R.id.btnViewChart)

        // Configure BMI Gauge
        bmiGauge.setMaxSpeed(40.0)
        bmiGauge.setMajorTickStep(5.0)
        bmiGauge.setMinorTicks(1)
        bmiGauge.addColoredRange(0.0, 18.5, 0xFF2196F3.toInt()) // Blue = Underweight
        bmiGauge.addColoredRange(18.5, 25.0, 0xFF4CAF50.toInt()) // Green = Normal
        bmiGauge.addColoredRange(25.0, 30.0, 0xFFFFC107.toInt()) // Yellow = Overweight
        bmiGauge.addColoredRange(30.0, 40.0, 0xFFF44336.toInt()) // Red = Obese

        btnCalculate.setOnClickListener {
            val age = inputAge.text.toString().toIntOrNull()
            val heightCm = inputHeight.text.toString().toDoubleOrNull()
            val weight = inputWeight.text.toString().toDoubleOrNull()

            if (age == null || heightCm == null || weight == null) {
                Toast.makeText(requireContext(), "Please enter all values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightM = heightCm / 100
            val bmi = weight / heightM.pow(2)

            val category = when {
                bmi < 18.5 -> "Underweight"
                bmi < 24.9 -> "Normal"
                bmi < 29.9 -> "Overweight"
                else -> "Obese"
            }

            val advice = when (category) {
                "Underweight" -> "You are underweight. Consider increasing your food intake."
                "Normal" -> "Great! You are at a healthy weight."
                "Overweight" -> "You are overweight. Try regular exercise and balanced diet."
                "Obese" -> "You are obese. Please consult a doctor or nutritionist."
                else -> ""
            }

            textResult.text = "Age: $age\nBMI: %.2f\nStatus: $category\n\n$advice".format(bmi)

            // Update Gauge
            bmiGauge.speed = bmi
        }

        btnViewChart.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_bmi_chart, null)

            val dialog = android.app.AlertDialog.Builder(requireContext())
                .setTitle("BMI Chart")
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .create()

            dialog.show()
        }
    }
}
