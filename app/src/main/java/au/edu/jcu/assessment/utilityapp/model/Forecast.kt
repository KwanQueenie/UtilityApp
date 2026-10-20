

package au.edu.jcu.assessment.utilityapp.model

import java.time.LocalDate
import java.time.LocalDateTime

/** Everything the main screen needs, already converted from the raw API response. */
data class Forecast(
    val current: CurrentConditions,
    /** Upcoming hours, starting with the current hour. */
    val hourly: List<HourForecast>,
    /** Seven days, starting with today. */
    val daily: List<DayForecast>,
    /** Today's sun protection window, or null when UV stays below 3 all day. */
    val sunProtection: SunProtectionWindow?,
    val todayUvMax: Double,
) {
    val today: DayForecast? get() = daily.firstOrNull()
}

data class CurrentConditions(
    val time: LocalDateTime,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeedKmh: Double,
    val windDirectionDegrees: Double,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val uvIndex: Double,
    val rainChance: Int,
)

data class HourForecast(
    val time: LocalDateTime,
    val temperature: Double,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val rainChance: Int,
    val uvIndex: Double,
)

data class DayForecast(
    val date: LocalDate,
    val condition: WeatherCondition,
    val maxTemperature: Double,
    val minTemperature: Double,
    val rainChance: Int,
    val uvMax: Double,
)