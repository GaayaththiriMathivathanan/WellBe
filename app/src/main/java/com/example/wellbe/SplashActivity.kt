package com.example.wellbe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.wellbe.databinding.ActivitySplashBinding
import com.example.wellbe.storage.Prefs

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme BEFORE showing splash
        val prefs = Prefs(this)
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load fade-in animation
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Apply animation to logo + title
        binding.splashLogo.startAnimation(fadeIn)
        binding.splashTitle.startAnimation(fadeIn)

        // Navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (prefs.isFirstLaunch()) {
                startActivity(Intent(this, OnboardingActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 2000) // 2 sec
    }
}
