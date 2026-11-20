package com.faizan.widgetstudio

import org.json.JSONObject

/** Strict, bounded v1 contract. Only the implemented Digital Clock is accepted. */
object DesignImport {
    private val HEX = Regex("^#[0-9a-fA-F]{6}$")
    fun parse(json: String): WidgetDesign? = try {
        if (json.toByteArray(Charsets.UTF_8).size > 16_384) null else {
            val o = JSONObject(json)
            val font = o.get("fontSize") as? Number
            val radius = o.get("radius") as? Number
            val text = o.getString("textColor")
            val bg = o.getString("backgroundColor")
            if (o.getString("format") != "mws-design" || o.get("version") != 1 ||
                o.getString("template") != "digital-clock" ||
                !HEX.matches(text) || !HEX.matches(bg) ||
                font == null || radius == null || !font.toDouble().isFinite() || !radius.toDouble().isFinite() ||
                o.get("showDate") !is Boolean || o.get("hour12") !is Boolean) null
            else WidgetDesign(
                textColor = (text.substring(1).toLong(16) or 0xFF000000L).toInt(),
                backgroundColor = (bg.substring(1).toLong(16) or 0xFF000000L).toInt(),
                fontSizeSp = font.toDouble().coerceIn(32.0, 96.0).toInt(),
                radiusDp = radius.toDouble().coerceIn(0.0, 48.0).toInt(),
                showDate = o.getBoolean("showDate"), hour12 = o.getBoolean("hour12"),
            )
        }
    } catch (_: Exception) { null }
}
