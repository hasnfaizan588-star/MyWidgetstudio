package com.faizan.widgetstudio

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch

/**
 * In-app designer screen. Changes preview live; "Apply" saves the design and
 * re-renders every Digital Clock widget on the home screen.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var design: WidgetDesign
    private lateinit var previewFrame: FrameLayout
    private lateinit var previewClock: android.widget.TextClock
    private lateinit var previewDate: android.widget.TextClock
    private lateinit var editTextColor: EditText
    private lateinit var editBgColor: EditText
    private lateinit var seekFontSize: SeekBar
    private lateinit var seekRadius: SeekBar
    private lateinit var labelFontSize: TextView
    private lateinit var labelRadius: TextView
    private lateinit var switchDate: MaterialSwitch
    private lateinit var switch24h: MaterialSwitch

    /** Guards against feedback loops while controls are being programmed. */
    private var updating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        design = WidgetSettings.load(this)

        previewFrame = findViewById(R.id.preview_frame)
        previewClock = findViewById(R.id.preview_clock)
        previewDate = findViewById(R.id.preview_date)
        editTextColor = findViewById(R.id.edit_text_color)
        editBgColor = findViewById(R.id.edit_bg_color)
        seekFontSize = findViewById(R.id.seek_font_size)
        seekRadius = findViewById(R.id.seek_radius)
        labelFontSize = findViewById(R.id.label_font_size)
        labelRadius = findViewById(R.id.label_radius)
        switchDate = findViewById(R.id.switch_date)
        switch24h = findViewById(R.id.switch_24h)

        bindControls()
        renderPreview()
    }

    private fun bindControls() {
        updating = true
        editTextColor.setText(hex(design.textColor))
        editBgColor.setText(hex(design.backgroundColor))
        seekFontSize.progress = (design.fontSizeSp - 20).coerceIn(0, seekFontSize.max)
        seekRadius.progress = design.radiusDp.coerceIn(0, seekRadius.max)
        switchDate.isChecked = design.showDate
        switch24h.isChecked = !design.hour12
        labelFontSize.text = getString(R.string.label_font_size_value, design.fontSizeSp)
        labelRadius.text = getString(R.string.label_radius_value, design.radiusDp)
        updating = false

        editTextColor.addTextChangedListener(HexWatcher {
            parseHex(it)?.let { c -> design = design.copy(textColor = c); renderPreview() }
        })
        editBgColor.addTextChangedListener(HexWatcher {
            parseHex(it)?.let { c -> design = design.copy(backgroundColor = c); renderPreview() }
        })

        val presetsText = listOf(
            R.id.preset_text_white to "#FFFFFF",
            R.id.preset_text_amber to "#FFB020",
            R.id.preset_text_red to "#FF5555",
            R.id.preset_text_cyan to "#40C4FF",
        )
        for ((id, value) in presetsText) {
            findViewById<View>(id).setOnClickListener {
                editTextColor.setText(value)
                design = design.copy(textColor = parseHex(value)!!)
                renderPreview()
            }
        }
        val presetsBg = listOf(
            R.id.preset_bg_black to "#0B0B0D",
            R.id.preset_bg_charcoal to "#17181C",
            R.id.preset_bg_navy to "#0E1626",
            R.id.preset_bg_white to "#F5F5F5",
        )
        for ((id, value) in presetsBg) {
            findViewById<View>(id).setOnClickListener {
                editBgColor.setText(value)
                design = design.copy(backgroundColor = parseHex(value)!!)
                renderPreview()
            }
        }

        seekFontSize.setOnSeekBarChangeListener(simpleSeek { progress ->
            design = design.copy(fontSizeSp = progress + 20)
            labelFontSize.text = getString(R.string.label_font_size_value, design.fontSizeSp)
            renderPreview()
        })
        seekRadius.setOnSeekBarChangeListener(simpleSeek { progress ->
            design = design.copy(radiusDp = progress)
            labelRadius.text = getString(R.string.label_radius_value, design.radiusDp)
            renderPreview()
        })

        switchDate.setOnCheckedChangeListener { _, checked ->
            if (!updating) { design = design.copy(showDate = checked); renderPreview() }
        }
        switch24h.setOnCheckedChangeListener { _, checked ->
            if (!updating) { design = design.copy(hour12 = !checked); renderPreview() }
        }

        findViewById<View>(R.id.btn_apply).setOnClickListener { apply() }
        findViewById<View>(R.id.btn_import).setOnClickListener { importPicker.launch(arrayOf("application/json", "text/plain", "*/*")) }
    }

    private fun renderPreview() {
        previewFrame.background = GradientDrawable().apply {
            setColor(design.backgroundColor)
            cornerRadius = dp(design.radiusDp)
        }
        WidgetViews.applyToPreview(previewClock, previewDate, design)
    }

    private val importPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@registerForActivityResult
        val text = try { contentResolver.openInputStream(uri)?.use { stream ->
            val output = java.io.ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            while (output.size() <= 16_384) {
                val count = stream.read(buffer, 0, minOf(buffer.size, 16_385 - output.size()))
                if (count < 0) break
                output.write(buffer, 0, count)
            }
            if (output.size() > 16_384) null else output.toString("UTF-8")
        } } catch (e: Exception) { null }
        val imported = text?.let { DesignImport.parse(it) }
        if (imported == null) {
            Toast.makeText(this, "Not a valid My Widget Studio clock design file", Toast.LENGTH_LONG).show()
        } else {
            WidgetSettings.save(this, imported)
            ClockWidgetProvider.pushAll(this)
            Toast.makeText(this, "Design imported and applied", Toast.LENGTH_SHORT).show()
            recreate()
        }
    }

    private fun apply() {
        WidgetSettings.save(this, design)
        ClockWidgetProvider.pushAll(this)
        val manager = AppWidgetManager.getInstance(this)
        val ids = manager.getAppWidgetIds(ComponentName(this, ClockWidgetProvider::class.java))
        if (ids.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_widget, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.toast_applied, Toast.LENGTH_SHORT).show()
        }
    }

    private fun dp(value: Int): Float = value * resources.displayMetrics.density

    private fun hex(color: Int): String = "#%06X".format(color and 0xFFFFFF)

    private fun parseHex(text: String): Int? {
        val t = text.trim().removePrefix("#")
        if (t.length != 6) return null
        return t.toLongOrNull(16)?.let { 0xFF000000.toInt() or it.toInt() }
    }

    private fun simpleSeek(onProgress: (Int) -> Unit): SeekBar.OnSeekBarChangeListener =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) onProgress(progress)
            }
            override fun onStartTrackingTouch(bar: SeekBar) = Unit
            override fun onStopTrackingTouch(bar: SeekBar) = Unit
        }

    private class HexWatcher(private val onValid: (String) -> Unit) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(s: Editable) {
            if (s.length == 7) onValid(s.toString())
        }
    }
}
