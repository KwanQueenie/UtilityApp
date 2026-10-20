package au.edu.jcu.assessment.utilityapp.data

import au.edu.jcu.assessment.utilityapp.api.ForecastApiService
import au.edu.jcu.assessment.utilityapp.api.GeocodingApiService
import au.edu.jcu.assessment.utilityapp.model.City
import au.edu.jcu.assessment.utilityapp.model.Forecast

/**
 * Repository pattern (Week 4): the ViewModel asks for weather through this interface and
 * never knows whether data comes from the network, a cache or a fake used in tests.
 */
interface WeatherRepository {
    suspend fun getForecast(city: City): Forecast
    suspend fun searchCities(query: String): List<City>
}

class NetworkWeatherRepository(
    private val forecastApi: ForecastApiService,
    private val geocodingApi: GeocodingApiService,
) : WeatherRepository {

    override suspend fun getForecast(city: City): Forecast =
        forecastApi.getForecast(latitude = city.latitude, longitude = city.longitude).toForecast()

    override suspend fun searchCities(query: String): List<City> =
        geocodingApi.searchCities(name = query).results
            .filter { it.countryCode == null || it.countryCode == "AU" }
            .map { it.toCity() }
            .distinctBy { it.label }
}