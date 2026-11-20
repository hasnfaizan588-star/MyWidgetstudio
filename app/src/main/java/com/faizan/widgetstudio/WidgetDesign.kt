package com.faizan.widgetstudio

/** The single widget design this app edits and applies to the home-screen widget. */
data class WidgetDesign(
    val textColor: Int = 0xFFFFFFFF.toInt(),
    val backgroundColor: Int = 0xFF0B0B0D.toInt(),
    val fontSizeSp: Int = 56,
    val radiusDp: Int = 28,
    val showDate: Boolean = true,
    val hour12: Boolean = true,
)

fun WidgetDesign.timePattern(): String = if (hour12) "hh:mm a" else "HH:mm"
