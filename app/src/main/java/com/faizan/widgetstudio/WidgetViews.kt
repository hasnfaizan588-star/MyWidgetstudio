package com.faizan.widgetstudio

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.TextClock
import android.widget.RemoteViews

/**
 * Builds the RemoteViews shown on the home screen from the saved [WidgetDesign].
 *
 * The clock uses TextClock, which keeps itself up to date on the launcher side —
 * no periodic alarms or WorkManager jobs are needed, so there is no battery drain
 * from scheduled updates.
 */
object WidgetViews {

    fun buildRemoteViews(context: Context, design: WidgetDesign): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_digital_clock)

        // Rounded background. The tint requires Android 12+ (RemoteViews.setColorStateList);
        // on older versions we fall back to a solid, square background colour.
        rv.setInt(R.id.widget_root, "setBackgroundResource", radiusDrawable(design.radiusDp))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rv.setColorStateList(
                R.id.widget_root,
                "setBackgroundTintList",
                ColorStateList.valueOf(design.backgroundColor),
            )
        } else {
            rv.setInt(R.id.widget_root, "setBackgroundColor", design.backgroundColor)
        }

        // Time text.
        rv.setTextColor(R.id.clock, design.textColor)
        rv.setTextViewTextSize(R.id.clock, TypedValue.COMPLEX_UNIT_SP, design.fontSizeSp.toFloat())
        rv.setCharSequence(R.id.clock, "setFormat12Hour", design.timePattern())
        rv.setCharSequence(R.id.clock, "setFormat24Hour", design.timePattern())

        // Optional date line.
        rv.setViewVisibility(R.id.date, if (design.showDate) View.VISIBLE else View.GONE)
        rv.setTextColor(R.id.date, withAlpha(design.textColor, 0.75f))
        rv.setTextViewTextSize(R.id.date, TypedValue.COMPLEX_UNIT_SP, dateSizeSp(design).toFloat())

        return rv
    }

    /** Maps a continuous radius (dp) onto the closest pre-made rounded drawable. */
    fun radiusDrawable(radiusDp: Int): Int = when {
        radiusDp <= 4 -> R.drawable.widget_bg_r0
        radiusDp <= 18 -> R.drawable.widget_bg_r12
        radiusDp <= 30 -> R.drawable.widget_bg_r24
        radiusDp <= 40 -> R.drawable.widget_bg_r36
        else -> R.drawable.widget_bg_r48
    }

    fun withAlpha(color: Int, alphaFactor: Float): Int {
        val alpha = ((color ushr 24) * alphaFactor).toInt().coerceIn(0, 255)
        return (alpha shl 24) or (color and 0x00FFFFFF)
    }

    fun dateSizeSp(design: WidgetDesign): Int = (design.fontSizeSp * 0.26f).toInt().coerceAtLeast(12)

    /** Formats the in-app preview TextClocks from a design. */
    fun applyToPreview(clock: TextClock, date: TextClock, design: WidgetDesign) {
        clock.setTextColor(design.textColor)
        clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, design.fontSizeSp.toFloat())
        clock.format12Hour = design.timePattern()
        clock.format24Hour = design.timePattern()
        date.setTextColor(withAlpha(design.textColor, 0.75f))
        date.setTextSize(TypedValue.COMPLEX_UNIT_SP, dateSizeSp(design).toFloat())
        date.format12Hour = "EEE, d MMM"
        date.format24Hour = "EEE, d MMM"
        date.visibility = if (design.showDate) View.VISIBLE else View.GONE
    }
}
