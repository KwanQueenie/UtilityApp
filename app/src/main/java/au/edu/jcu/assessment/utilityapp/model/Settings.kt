

package au.edu.jcu.assessment.utilityapp.model

import kotlin.math.roundToInt

/** User preferences. Held in memory only – the brief says settings need not persist. */
data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val headline: HeadlineTemperature = HeadlineTemperature.ACTUAL,
    val windUnit: WindUnit = WindUnit.KMH,
    val showSunProtection: Boolean = true,
)

enum class ThemeMode(val label: String) {
    SYSTEM("Auto"),
    LIGHT("Light"),
    DARK("Dark"),
    /** Dark after local sunset, light during the day – matches the city being viewed. */
    SUNSET("Sunset"),
}

enum class HeadlineTemperature(val label: String) {
    ACTUAL("Actual"),
    FEELS_LIKE("Feels like"),
}

enum class WindUnit(val label: String) {
    KMH("km/h"),
    /** Knots are the norm for boating and fishing around the Australian coast. */
    KNOTS("kn");

    fun fromKmh(kmh: Double): Double = when (this) {
        KMH -> kmh
        KNOTS -> kmh * KMH_TO_KNOTS
    }

    private companion object {
        const val KMH_TO_KNOTS = 0.539957
    }
}

/** Converts a meteorological wind direction (degrees the wind blows FROM) to N, NE, E … */
fun compassDirection(degrees: Double): String {
    val points = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val normalised = ((degrees % 360) + 360) % 360
    return points[(normalised / 45.0).roundToInt() % points.size]
}