package au.edu.jcu.assessment.utilityapp.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Open-Meteo forecast API (free, no API key). Docs: https://open-meteo.com/en/docs
 * timezone=auto makes every time in the response local to the requested city,
 * so Queensland (no daylight saving) and NSW (daylight saving) both display correctly.
 */
interface ForecastApiService {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_FIELDS,
        @Query("hourly") hourly: String = HOURLY_FIELDS,
        @Query("daily") daily: String = DAILY_FIELDS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
    ): ForecastResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"

        const val CURRENT_FIELDS =
            "temperature_2m,apparent_temperature,relative_humidity_2m,weather_code," +
                    "wind_speed_10m,wind_direction_10m,is_day"
        const val HOURLY_FIELDS =
            "temperature_2m,weather_code,precipitation_probability,uv_index,is_day"
        const val DAILY_FIELDS =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max,uv_index_max"
    }
}