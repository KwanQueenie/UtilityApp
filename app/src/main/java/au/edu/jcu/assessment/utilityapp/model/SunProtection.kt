package au.edu.jcu.assessment.utilityapp.model

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToLong

/** The part of the day when the UV index is at or above the protection threshold. */
data class SunProtectionWindow(
    val start: LocalTime,
    val end: LocalTime,
    val peakUv: Double,
)

object SunProtectionCalculator {

    /**
     * Works out today's sun protection times from hourly UV values.
     * The API only gives one value per hour, so the exact start and end minutes are
     * estimated by linear interpolation between the two hours either side of the threshold.
     */
    fun forDay(
        hours: List<HourForecast>,
        threshold: Double = UvLevel.PROTECTION_THRESHOLD,
    ): SunProtectionWindow? {
        val sorted = hours.sortedBy { it.time }
        val firstIndex = sorted.indexOfFirst { it.uvIndex >= threshold }
        if (firstIndex == -1) return null
        val lastIndex = sorted.indexOfLast { it.uvIndex >= threshold }

        val start = if (firstIndex > 0) {
            crossingTime(sorted[firstIndex - 1], sorted[firstIndex], threshold)
        } else {
            sorted[firstIndex].time
        }
        val end = if (lastIndex < sorted.lastIndex) {
            crossingTime(sorted[lastIndex], sorted[lastIndex + 1], threshold)
        } else {
            sorted[lastIndex].time
        }
        return SunProtectionWindow(
            start = start.toLocalTime(),
            end = end.toLocalTime(),
            peakUv = sorted.maxOf { it.uvIndex },
        )
    }

    private fun crossingTime(a: HourForecast, b: HourForecast, threshold: Double): LocalDateTime {
        val change = b.uvIndex - a.uvIndex
        val fraction = if (change == 0.0) 0.0 else ((threshold - a.uvIndex) / change).coerceIn(0.0, 1.0)
        val minutes = Duration.between(a.time, b.time).toMinutes() * fraction
        return a.time.plusMinutes(minutes.roundToLong())
    }
}