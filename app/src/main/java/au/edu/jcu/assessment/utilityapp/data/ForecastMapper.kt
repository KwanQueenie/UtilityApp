package au.edu.jcu.assessment.utilityapp.data

import au.edu.jcu.assessment.utilityapp.api.DailyDto
import au.edu.jcu.assessment.utilityapp.api.ForecastResponse
import au.edu.jcu.assessment.utilityapp.api.GeocodingResultDto
import au.edu.jcu.assessment.utilityapp.api.HourlyDto
import au.edu.jcu.assessment.utilityapp.model.AustralianCities
import au.edu.jcu.assessment.utilityapp.model.City
import au.edu.jcu.assessment.utilityapp.model.CurrentConditions
import au.edu.jcu.assessment.utilityapp.model.DayForecast
import au.edu.jcu.assessment.utilityapp.model.Forecast
import au.edu.jcu.assessment.utilityapp.model.HourForecast
import au.edu.jcu.assessment.utilityapp.model.SunProtectionCalculator
import au.edu.jcu.assessment.utilityapp.model.WeatherCondition
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

/** How many upcoming hours the "Today" strip shows. */
internal const val HOURS_TO_SHOW = 24

private const val UNKNOWN_WEATHER_CODE = 3 // treat missing codes as "cloudy"

/** Converts the raw API response into the app's [Forecast] model. */
internal fun ForecastResponse.toForecast(): Forecast {
    val now = LocalDateTime.parse(current.time)
    val currentHour = now.truncatedTo(ChronoUnit.HOURS)
    val allHours = hourly.toHourForecasts()
    val thisHour = allHours.firstOrNull { it.time == currentHour }
    val today = now.toLocalDate()
    val days = daily.toDayForecasts()

    return Forecast(
        current = CurrentConditions(
            time = now,
            temperature = current.temperature,
            feelsLike = current.apparentTemperature,
            humidity = current.humidity.roundToInt(),
            windSpeedKmh = current.windSpeed,
            windDirectionDegrees = current.windDirection,
            condition = WeatherCondition.fromWmoCode(current.weatherCode),
            isDay = current.isDay == 1,
            // UV and rain chance are hourly-only values, so take them from the current hour.
            uvIndex = thisHour?.uvIndex ?: 0.0,
            rainChance = thisHour?.rainChance ?: 0,
        ),
        hourly = allHours.filter { !it.time.isBefore(currentHour) }.take(HOURS_TO_SHOW),
        daily = days,
        sunProtection = SunProtectionCalculator.forDay(allHours.filter { it.time.toLocalDate() == today }),
        todayUvMax = days.firstOrNull()?.uvMax ?: 0.0,
    )
}

private fun HourlyDto.toHourForecasts(): List<HourForecast> =
    time.indices.mapNotNull { i ->
        val temperatureAtHour = temperature.getOrNull(i) ?: return@mapNotNull null
        HourForecast(
            time = LocalDateTime.parse(time[i]),
            temperature = temperatureAtHour,
            condition = WeatherCondition.fromWmoCode(weatherCode.getOrNull(i) ?: UNKNOWN_WEATHER_CODE),
            isDay = (isDay.getOrNull(i) ?: 1) == 1,
            rainChance = precipitationProbability.getOrNull(i) ?: 0,
            uvIndex = uvIndex.getOrNull(i) ?: 0.0,
        )
    }

private fun DailyDto.toDayForecasts(): List<DayForecast> =
    time.indices.mapNotNull { i ->
        val max = temperatureMax.getOrNull(i) ?: return@mapNotNull null
        val min = temperatureMin.getOrNull(i) ?: return@mapNotNull null
        DayForecast(
            date = LocalDate.parse(time[i]),
            condition = WeatherCondition.fromWmoCode(weatherCode.getOrNull(i) ?: UNKNOWN_WEATHER_CODE),
            maxTemperature = max,
            minTemperature = min,
            rainChance = precipitationProbabilityMax.getOrNull(i) ?: 0,
            uvMax = uvIndexMax.getOrNull(i) ?: 0.0,
        )
    }

internal fun GeocodingResultDto.toCity(): City = City(
    name = name,
    region = AustralianCities.stateCode(admin1),
    latitude = latitude,
    longitude = longitude,
)