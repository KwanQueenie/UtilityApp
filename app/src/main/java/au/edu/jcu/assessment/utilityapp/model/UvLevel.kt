package au.edu.jcu.assessment.utilityapp.model

import kotlin.math.roundToInt

/** WHO UV index categories, as used by the Bureau of Meteorology and Cancer Council Australia. */
enum class UvLevel(val label: String, private val minIndex: Int) {
    LOW("Low", 0),
    MODERATE("Moderate", 3),
    HIGH("High", 6),
    VERY_HIGH("Very high", 8),
    EXTREME("Extreme", 11);

    companion object {
        /** Sun protection is recommended in Australia whenever the UV index is 3 or above. */
        const val PROTECTION_THRESHOLD = 3.0

        fun from(uvIndex: Double): UvLevel {
            val rounded = uvIndex.roundToInt()
            return entries.last { rounded >= it.minIndex }
        }
    }
}