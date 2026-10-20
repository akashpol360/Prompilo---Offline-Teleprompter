package com.prompilo.app

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatTimeTest {
    @Test fun formatsRecordingTimer() {
        assertEquals("00:00", formatTime(0))
        assertEquals("01:05", formatTime(65_000))
        assertEquals("12:34", formatTime(754_000))
    }
}
