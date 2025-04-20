package pl.pelotasplus.queens.core

import junit.framework.TestCase.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    fun `test formatTime`() {
        assertEquals("02:00", formatTime(120L))
        assertEquals("01:30", formatTime(90L))
        assertEquals("00:00", formatTime(0L))
        assertEquals("60:00", formatTime(60 * 60L))
        assertEquals("600:00", formatTime(10 * 60 * 60L))
    }
}
