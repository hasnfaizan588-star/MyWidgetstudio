package com.faizan.widgetstudio

import org.junit.Assert.*
import org.junit.Test

class DesignImportTest {
    private val valid = """{"format":"mws-design","version":1,"template":"digital-clock","name":"Phone clock","textColor":"#ff0000","backgroundColor":"#123456","fontSize":64,"radius":28,"showDate":false,"hour12":true}"""
    @Test fun importsConcreteWebValues() {
        val d = requireNotNull(DesignImport.parse(valid))
        assertEquals(0xFFFF0000.toInt(), d.textColor)
        assertEquals(0xFF123456.toInt(), d.backgroundColor)
        assertEquals(64, d.fontSizeSp)
        assertEquals(28, d.radiusDp)
        assertFalse(d.showDate)
        assertTrue(d.hour12)
    }
    @Test fun clampsNumericValues() {
        val d = requireNotNull(DesignImport.parse(valid.replace("\"fontSize\":64", "\"fontSize\":999").replace("\"radius\":28", "\"radius\":-10")))
        assertEquals(96, d.fontSizeSp)
        assertEquals(0, d.radiusDp)
    }
    @Test fun rejectsUnsupportedOrMalformedFiles() {
        assertNull(DesignImport.parse(valid.replace("digital-clock", "minimal-clock")))
        assertNull(DesignImport.parse(valid.replace("\"version\":1", "\"version\":2")))
        assertNull(DesignImport.parse(valid.replace("#ff0000", "red")))
        assertNull(DesignImport.parse(valid.replace("\"hour12\":true", "\"hour12\":\"true\"")))
        assertNull(DesignImport.parse("{"))
        assertNull(DesignImport.parse(" ".repeat(16_385)))
    }
    @Test fun explicitFormatOverridesPhonePreference() {
        assertEquals("hh:mm a", WidgetDesign(hour12 = true).timePattern())
        assertEquals("HH:mm", WidgetDesign(hour12 = false).timePattern())
    }
}
