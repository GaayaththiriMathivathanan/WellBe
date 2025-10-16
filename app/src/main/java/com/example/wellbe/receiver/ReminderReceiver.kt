package com.example.wellbe.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.wellbe.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: "Reminder"

        val message = when (type) {
            "hydration" -> "Stay hydrated! Drink a glass of water 💧"
            "mood" -> "How are you feeling right now? 🙂"
            else -> "Don't forget your health goals!"
        }

        // Example progress: hydration goal = 8 glasses
        val prefs = com.example.wellbe.storage.Prefs(context)
        val goal = if (type == "hydration") prefs.getWaterGoal() else 100
        val current = if (type == "hydration") prefs.getWaterCount() else 50

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_notification)   //  use your app icon
            .setContentTitle("WellBe Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)                           // stays like "Walk" card
            .setOnlyAlertOnce(true)
            .setProgress(goal, current, false)          //  progress bar
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(type.hashCode(), notification)
    }
}
