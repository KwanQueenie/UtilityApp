package au.edu.jcu.assessment.utilityapp.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WeatherModelTest {

    @Test
    fun wmoCodes_mapToExpectedConditions() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromWmoCode(0))
        assertEquals(WeatherCondition.PARTLY_CLOUDY, WeatherCondition.fromWmoCode(2))
        assertEquals(WeatherCondition.FOG, WeatherCondition.fromWmoCode(45))
        assertEquals(WeatherCondition.DRIZZLE, WeatherCondition.fromWmoCode(53))
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromWmoCode(80))
        assertEquals(WeatherCondition.HEAVY_RAIN, WeatherCondition.fromWmoCode(82))
        assertEquals(WeatherCondition.SNOW, WeatherCondition.fromWmoCode(75))
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCondition.fromWmoCode(95))
        assertEquals(WeatherCondition.OVERCAST, WeatherCondition.fromWmoCode(999)) // unknown code
    }

    @Test
    fun clearSky_usesSunByDayAndMoonByNight() {
        assertEquals(IllustrationKind.SUN, WeatherCondition.CLEAR.illustration(isDay = true))
        assertEquals(IllustrationKind.MOON, WeatherCondition.CLEAR.illustration(isDay = false))
        assertEquals("sunny", WeatherCondition.CLEAR.describe(isDay = true))
    }

    @Test
    fun uvLevels_followWhoScale() {
        assertEquals(UvLevel.LOW, UvLevel.from(2.4))
        assertEquals(UvLevel.MODERATE, UvLevel.from(2.6)) // rounds to 3
        assertEquals(UvLevel.HIGH, UvLevel.from(7.0))
        assertEquals(UvLevel.VERY_HIGH, UvLevel.from(10.0))
        assertEquals(UvLevel.EXTREME, UvLevel.from(13.2))
    }

    @Test
    fun windDirection_isConvertedToCompassPoint() {
        assertEquals("N", compassDirection(350.0))
        assertEquals("SE", compassDirection(135.0))
        assertEquals("W", compassDirection(270.0))
        assertEquals("N", compassDirection(-10.0))
    }

    @Test
    fun knots_conversionIsCorrect() {
        assertEquals(10.8, WindUnit.KNOTS.fromKmh(20.0), 0.01)
        assertEquals(20.0, WindUnit.KMH.fromKmh(20.0), 0.0)
    }

    @Test
    fun stateNames_areShortened() {
        assertEquals("QLD", AustralianCities.stateCode("Queensland"))
        assertEquals("WA", AustralianCities.stateCode("Western Australia"))
        assertEquals("", AustralianCities.stateCode(null))
    }
}