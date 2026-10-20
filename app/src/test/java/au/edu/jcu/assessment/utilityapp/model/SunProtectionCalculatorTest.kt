package au.edu.jcu.assessment.utilityapp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.LocalTime

class SunProtectionCalculatorTest {

    private fun hour(h: Int, uv: Double) = HourForecast(
        time = LocalDateTime.of(2026, 10, 20, h, 0),
        temperature = 25.0,
        condition = WeatherCondition.CLEAR,
        isDay = true,
        rainChance = 0,
        uvIndex = uv,
    )

    @Test
    fun window_isInterpolatedBetweenHours() {
        val hours = listOf(hour(7, 1.0), hour(8, 2.0), hour(9, 4.0), hour(12, 11.0), hour(15, 4.0), hour(16, 2.0))
        val window = SunProtectionCalculator.forDay(hours)!!
        assertEquals(LocalTime.of(8, 30), window.start) // UV 2 -> 4 crosses 3 halfway
        assertEquals(LocalTime.of(15, 30), window.end) // UV 4 -> 2 crosses 3 halfway
        assertEquals(11.0, window.peakUv, 0.0)
    }

    @Test
    fun lowUvDay_hasNoWindow() {
        assertNull(SunProtectionCalculator.forDay(listOf(hour(10, 1.0), hour(12, 2.9), hour(14, 2.0))))
    }

    @Test
    fun unsortedInput_isHandled() {
        val window = SunProtectionCalculator.forDay(listOf(hour(10, 5.0), hour(9, 3.0), hour(11, 1.0)))!!
        assertEquals(LocalTime.of(9, 0), window.start)
        assertEquals(LocalTime.of(10, 30), window.end)
    }
}