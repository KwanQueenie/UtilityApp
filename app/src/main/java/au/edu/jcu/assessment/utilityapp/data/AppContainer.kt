package au.edu.jcu.assessment.utilityapp.data

import au.edu.jcu.assessment.utilityapp.api.ForecastApiService
import au.edu.jcu.assessment.utilityapp.api.GeocodingApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Manual dependency injection container (Week 4). It is created once in
 * [au.edu.jcu.assessment.utilityapp.SoutherlyApplication] and hands out shared dependencies.
 * Swapping in a different repository (e.g. a fake for tests) only needs a new container.
 */
interface AppContainer {
    val weatherRepository: WeatherRepository
}

class DefaultAppContainer : AppContainer {

    private val json = Json { ignoreUnknownKeys = true }

    private val httpClient = OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .build()

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val forecastApi: ForecastApiService by lazy {
        retrofit(ForecastApiService.BASE_URL).create(ForecastApiService::class.java)
    }

    private val geocodingApi: GeocodingApiService by lazy {
        retrofit(GeocodingApiService.BASE_URL).create(GeocodingApiService::class.java)
    }

    override val weatherRepository: WeatherRepository by lazy {
        NetworkWeatherRepository(forecastApi, geocodingApi)
    }
}