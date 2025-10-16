package com.example.wellbe

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.wellbe.receiver.ResetReceiver
import com.example.wellbe.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // ✅ Create notification channel on startup
        createNotificationChannel()

        // ✅ Send one test notification so app shows in system notification settings
        sendTestNotification()

        // Show Home by default
        if (savedInstanceState == null) {
            openFragment(HomeFragment(), false) // don’t add Home to backstack
            bottomNav.selectedItemId = R.id.nav_home
        }

        // Handle bottom navigation clicks
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    openFragment(HomeFragment(), false)
                    true
                }
                R.id.nav_habits -> {
                    openFragment(HabitFragment())
                    true
                }
                R.id.nav_mood -> {
                    openFragment(MoodFragment())
                    true
                }
                R.id.nav_steps -> {
                    openFragment(StepsFragment())
                    true
                }
                R.id.nav_bmi -> {
                    openFragment(BmiFragment())
                    true
                }
                R.id.nav_settings -> {
                    openFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        // ✅ Handle back press with dispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fm = supportFragmentManager
                if (fm.backStackEntryCount > 0) {
                    fm.popBackStack() // Go back in fragment stack
                } else {
                    finish() // Exit app
                }
            }
        })

        // ✅ Daily reset at midnight
        scheduleDailyReset()
    }

    private fun openFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
            if (addToBackStack) addToBackStack(null)
        }
    }

    private fun scheduleDailyReset() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ResetReceiver::class.java).apply {
            action = "com.example.wellbe.DAILY_RESET"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val midnight = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            midnight.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // ✅ Create channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Hydration and Mood reminders"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // ✅ Send one test notification
    private fun sendTestNotification() {
        val notification = NotificationCompat.Builder(this, "reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("WellBe Test Notification")
            .setContentText("If you see this, notifications are working 🎉")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(1001, notification)
    }
}
