package au.edu.jcu.assessment.utilityapp.data

import au.edu.jcu.assessment.utilityapp.api.CurrentDto
import au.edu.jcu.assessment.utilityapp.api.DailyDto
import au.edu.jcu.assessment.utilityapp.api.ForecastResponse
import au.edu.jcu.assessment.utilityapp.api.HourlyDto
import au.edu.jcu.assessment.utilityapp.model.WeatherCondition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime

class ForecastMapperTest {

    private val response = ForecastResponse(
        timezone = "Australia/Brisbane",
        current = CurrentDto(
            time = "2026-10-20T10:15",
            temperature = 27.3,
            apparentTemperature = 29.8,
            humidity = 70.0,
            weatherCode = 95,
            windSpeed = 12.0,
            windDirection = 140.0,
            isDay = 1,
        ),
        hourly = HourlyDto(
            time = (6..14).map { "2026-10-20T%02d:00".format(it) },
            temperature = listOf(22.0, 23.0, 24.0, 25.0, 27.0, 28.0, 29.0, null, 30.0),
            weatherCode = listOf(0, 0, 1, 2, 95, 95, 3, 3, 3),
            precipitationProbability = listOf(0, 0, 5, 10, 60, 70, 20, 10, 0),
            uvIndex = listOf(0.0, 1.0, 2.0, 4.0, 6.0, 9.0, 10.0, 9.0, 7.0),
            isDay = listOf(1, 1, 1, 1, 1, 1, 1, 1, 1),
        ),
        daily = DailyDto(
            time = listOf("2026-10-20"),
            weatherCode = listOf(95),
            temperatureMax = listOf(31.0),
            temperatureMin = listOf(22.0),
            precipitationProbabilityMax = listOf(70),
            uvIndexMax = listOf(10.0),
        ),
    )

    @Test
    fun hourlyForecast_startsAtCurrentHour_andSkipsMissingValues() {
        val forecast = response.toForecast()
        assertEquals(LocalDateTime.of(2026, 10, 20, 10, 0), forecast.hourly.first().time)
        assertEquals(4, forecast.hourly.size) // 10, 11, 12, 14 (13:00 had no temperature)
    }

    @Test
    fun currentConditions_takeUvAndRainFromCurrentHour() {
        val current = response.toForecast().current
        assertEquals(WeatherCondition.THUNDERSTORM, current.condition)
        assertEquals(6.0, current.uvIndex, 0.0)
        assertEquals(60, current.rainChance)
    }

    @Test
    fun sunProtectionWindow_isCalculatedForToday() {
        val forecast = response.toForecast()
        assertNotNull(forecast.sunProtection)
        assertEquals(10.0, forecast.todayUvMax, 0.0)
    }
}