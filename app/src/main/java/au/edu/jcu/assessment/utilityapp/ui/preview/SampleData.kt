package au.edu.jcu.assessment.utilityapp.ui.preview

import au.edu.jcu.assessment.utilityapp.model.CurrentConditions
import au.edu.jcu.assessment.utilityapp.model.DayForecast
import au.edu.jcu.assessment.utilityapp.model.Forecast
import au.edu.jcu.assessment.utilityapp.model.HourForecast
import au.edu.jcu.assessment.utilityapp.model.SunProtectionWindow
import au.edu.jcu.assessment.utilityapp.model.WeatherCondition
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Fixed data so Android Studio @Preview can render screens without the network. */
object SampleData {
    private val now = LocalDateTime.of(2026, 10, 20, 12, 0)

    private val hourlyConditions = listOf(
        WeatherCondition.THUNDERSTORM, WeatherCondition.RAIN, WeatherCondition.OVERCAST,
        WeatherCondition.PARTLY_CLOUDY, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.MAINLY_CLEAR,
        WeatherCondition.CLEAR, WeatherCondition.CLEAR, WeatherCondition.CLEAR, WeatherCondition.CLEAR,
    )
    private val hourlyTemps = listOf(25.0, 26.4, 28.6, 29.1, 27.8, 26.0, 24.2, 23.1, 22.5, 22.0)
    private val hourlyRain = listOf(70, 55, 30, 10, 5, 0, 0, 0, 0, 0)

    val forecast = Forecast(
        current = CurrentConditions(
            time = now,
            temperature = 25.2,
            feelsLike = 27.4,
            humidity = 78,
            windSpeedKmh = 14.0,
            windDirectionDegrees = 135.0,
            condition = WeatherCondition.THUNDERSTORM,
            isDay = true,
            uvIndex = 9.0,
            rainChance = 70,
        ),
        hourly = hourlyTemps.indices.map { i ->
            HourForecast(
                time = now.plusHours(i.toLong()),
                temperature = hourlyTemps[i],
                condition = hourlyConditions[i],
                isDay = i < 7,
                rainChance = hourlyRain[i],
                uvIndex = 0.0,
            )
        },
        daily = listOf(
            WeatherCondition.THUNDERSTORM to (31.0 to 22.0),
            WeatherCondition.PARTLY_CLOUDY to (30.0 to 21.0),
            WeatherCondition.CLEAR to (29.0 to 20.0),
            WeatherCondition.RAIN to (27.0 to 21.0),
            WeatherCondition.OVERCAST to (28.0 to 22.0),
            WeatherCondition.MAINLY_CLEAR to (30.0 to 21.0),
            WeatherCondition.CLEAR to (31.0 to 22.0),
        ).mapIndexed { i, (condition, temps) ->
            DayForecast(
                date = LocalDate.of(2026, 10, 20).plusDays(i.toLong()),
                condition = condition,
                maxTemperature = temps.first,
                minTemperature = temps.second,
                rainChance = if (condition == WeatherCondition.RAIN) 80 else 10,
                uvMax = 11.0,
            )
        },
        sunProtection = SunProtectionWindow(LocalTime.of(8, 10), LocalTime.of(15, 50), 12.0),
        todayUvMax = 12.0,
    )
}