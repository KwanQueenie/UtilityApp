package au.edu.jcu.assessment.utilityapp.model

/**
 * Simplified weather conditions grouped from WMO weather codes
 * (https://open-meteo.com/en/docs, "WMO Weather interpretation codes").
 */
enum class WeatherCondition(val label: String, private val phrase: String) {
    CLEAR("Clear", "clear"),
    MAINLY_CLEAR("Mostly clear", "mostly clear"),
    PARTLY_CLOUDY("Partly cloudy", "partly cloudy"),
    OVERCAST("Cloudy", "cloudy"),
    FOG("Fog", "foggy"),
    DRIZZLE("Drizzle", "drizzly"),
    RAIN("Rain", "rainy"),
    HEAVY_RAIN("Heavy rain", "pouring"),
    SNOW("Snow", "snowy"),
    THUNDERSTORM("Storm", "stormy");

    /** Word used in the header sentence: "It's sunny now". */
    fun describe(isDay: Boolean): String = when (this) {
        CLEAR -> if (isDay) "sunny" else "clear"
        MAINLY_CLEAR -> if (isDay) "mostly sunny" else "mostly clear"
        else -> phrase
    }

    /** Which drawing to show for this condition at this time of day. */
    fun illustration(isDay: Boolean): IllustrationKind = when (this) {
        CLEAR -> if (isDay) IllustrationKind.SUN else IllustrationKind.MOON
        MAINLY_CLEAR, PARTLY_CLOUDY -> if (isDay) IllustrationKind.SUN_CLOUD else IllustrationKind.MOON_CLOUD
        OVERCAST -> IllustrationKind.CLOUD
        FOG -> IllustrationKind.FOG
        DRIZZLE -> IllustrationKind.DRIZZLE
        RAIN -> if (isDay) IllustrationKind.RAIN else IllustrationKind.NIGHT_RAIN
        HEAVY_RAIN -> IllustrationKind.HEAVY_RAIN
        SNOW -> IllustrationKind.SNOW
        THUNDERSTORM -> IllustrationKind.STORM
    }

    companion object {
        fun fromWmoCode(code: Int): WeatherCondition = when (code) {
            0 -> CLEAR
            1 -> MAINLY_CLEAR
            2 -> PARTLY_CLOUDY
            3 -> OVERCAST
            45, 48 -> FOG
            in 51..57 -> DRIZZLE
            61, 63, 66, 80, 81 -> RAIN
            65, 67, 82 -> HEAVY_RAIN
            in 71..77, 85, 86 -> SNOW
            95, 96, 99 -> THUNDERSTORM
            else -> OVERCAST
        }
    }
}

/** The set of weather drawings the UI knows how to render. */
enum class IllustrationKind {
    SUN, MOON, SUN_CLOUD, MOON_CLOUD, CLOUD, FOG, DRIZZLE, RAIN, NIGHT_RAIN, HEAVY_RAIN, SNOW, STORM
}