package au.edu.jcu.assessment.utilityapp.api

import retrofit2.http.GET
import retrofit2.http.Query

/** Open-Meteo geocoding API, used by the city search in Settings. */
interface GeocodingApiService {

    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
        @Query("countryCode") countryCode: String = "AU",
    ): GeocodingResponse

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/"
    }
}