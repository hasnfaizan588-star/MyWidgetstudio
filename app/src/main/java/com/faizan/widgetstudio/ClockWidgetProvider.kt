package com.faizan.widgetstudio

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context

/**
 * The home-screen digital clock widget.
 *
 * Updates are pushed whenever the design changes in the app; between pushes the
 * TextClock keeps itself current, so no background work is scheduled at all.
 */
class ClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        push(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun push(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            if (appWidgetIds.isEmpty()) return
            val design = WidgetSettings.load(context)
            for (id in appWidgetIds) {
                appWidgetManager.updateAppWidget(id, WidgetViews.buildRemoteViews(context, design))
            }
        }

        /** Re-renders every clock widget the user has on the home screen. */
        fun pushAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ClockWidgetProvider::class.java))
            push(context, manager, ids)
        }
    }
}
