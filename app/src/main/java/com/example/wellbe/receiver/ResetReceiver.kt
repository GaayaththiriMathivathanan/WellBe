package com.example.wellbe.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wellbe.storage.Prefs

class ResetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = Prefs(context)

        // Reset daily values
        prefs.resetDaily()

        // Send broadcast so UI (StepsFragment, etc.) updates immediately
        val resetIntent = Intent("com.example.wellbe.DAILY_RESET")
        context.sendBroadcast(resetIntent)
    }
}
