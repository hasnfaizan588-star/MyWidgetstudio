package com.faizan.widgetstudio

import android.content.Context

object WidgetSettings {
    private const val PREFS = "widget_design"

    fun load(context: Context): WidgetDesign {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return WidgetDesign(
            textColor = p.getInt("textColor", 0xFFFFFFFF.toInt()),
            backgroundColor = p.getInt("backgroundColor", 0xFF0B0B0D.toInt()),
            fontSizeSp = p.getInt("fontSizeSp", 56),
            radiusDp = p.getInt("radiusDp", 28),
            showDate = p.getBoolean("showDate", true),
            hour12 = p.getBoolean("hour12", true),
        )
    }

    fun save(context: Context, design: WidgetDesign) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putInt("textColor", design.textColor)
            .putInt("backgroundColor", design.backgroundColor)
            .putInt("fontSizeSp", design.fontSizeSp)
            .putInt("radiusDp", design.radiusDp)
            .putBoolean("showDate", design.showDate)
            .putBoolean("hour12", design.hour12)
            .apply()
    }
}
