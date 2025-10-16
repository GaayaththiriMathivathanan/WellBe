package com.example.wellbe.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.wellbe.MainActivity
import com.example.wellbe.R
import com.example.wellbe.storage.Prefs

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = Prefs(context)
            val completion = prefs.getTodayCompletionPercent()

            val views = RemoteViews(context.packageName, R.layout.widget_habit).apply {
                setTextViewText(R.id.widgetTitle, "WellBe Habits")
                setTextViewText(R.id.widgetProgress, "$completion% Completed")

                // Open app when tapping the widget
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_IMMUTABLE
                )
                setOnClickPendingIntent(R.id.widgetTitle, pendingIntent)
                setOnClickPendingIntent(R.id.widgetProgress, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
