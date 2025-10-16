package com.example.wellbe

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.wellbe.storage.Prefs   // 👈 make sure this is here
// also import your adapter + model if you already created them
// import com.example.wellbe.ui.onboarding.OnboardingAdapter
// import com.example.wellbe.ui.onboarding.OnboardingItem

class OnboardingActivity : AppCompatActivity() {   // 👈 class starts here

    private lateinit var viewPager: ViewPager2
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button
    private lateinit var layoutDots: LinearLayout
    private lateinit var prefs: Prefs

    private lateinit var items: List<OnboardingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        prefs = Prefs(this)

        items = listOf(
            OnboardingItem(R.drawable.ic_bmi, "Track BMI", "Calculate and monitor your BMI easily."),
            OnboardingItem(R.drawable.ic_steps, "Step Counter", "Track your daily steps and stay active."),
            OnboardingItem(R.drawable.ic_hydration, "Hydration", "Set reminders to drink enough water.")
        )

        viewPager = findViewById(R.id.viewPager)
        btnSkip = findViewById(R.id.btnSkip)
        btnNext = findViewById(R.id.btnNext)
        layoutDots = findViewById(R.id.layoutDots)

        viewPager.adapter = OnboardingAdapter(items)

        addDots(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addDots(position)
                btnNext.text = if (position == items.size - 1) "Finish" else "Next"
            }
        })

        btnSkip.setOnClickListener { completeOnboarding() }
        btnNext.setOnClickListener {
            if (viewPager.currentItem < items.size - 1) {
                viewPager.currentItem += 1
            } else {
                completeOnboarding()
            }
        }
    }

    private fun addDots(current: Int) {
        layoutDots.removeAllViews()
        for (i in items.indices) {
            val dot = TextView(this).apply {
                text = "•"
                textSize = 32f
                setTextColor(if (i == current) getColor(R.color.primaryColor) else Color.GRAY)
            }
            layoutDots.addView(dot)
        }
    }

    private fun completeOnboarding() {
        prefs.setFirstLaunch(false)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
