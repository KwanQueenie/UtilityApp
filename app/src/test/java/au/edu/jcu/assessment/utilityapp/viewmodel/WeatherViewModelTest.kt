package au.edu.jcu.assessment.utilityapp.viewmodel

import au.edu.jcu.assessment.utilityapp.data.WeatherRepository
import au.edu.jcu.assessment.utilityapp.model.AustralianCities
import au.edu.jcu.assessment.utilityapp.model.City
import au.edu.jcu.assessment.utilityapp.model.Forecast
import au.edu.jcu.assessment.utilityapp.model.ThemeMode
import au.edu.jcu.assessment.utilityapp.ui.preview.SampleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

/** Fake repository: lets tests control what the "network" returns. */
private class FakeWeatherRepository : WeatherRepository {
    var failWith: Exception? = null
    val requestedCities = mutableListOf<City>()
    var searchResults = listOf(City("Magnetic Island", "QLD", -19.15, 146.85))

    override suspend fun getForecast(city: City): Forecast {
        requestedCities += city
        failWith?.let { throw it }
        return SampleData.forecast
    }

    override suspend fun searchCities(query: String): List<City> = searchResults
}

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    private lateinit var repository: FakeWeatherRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = FakeWeatherRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsTownsvilleForecast() {
        val viewModel = WeatherViewModel(repository)
        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
        assertEquals(AustralianCities.Townsville, repository.requestedCities.single())
    }

    @Test
    fun networkFailure_showsErrorState() {
        repository.failWith = IOException("offline")
        val viewModel = WeatherViewModel(repository)
        assertTrue(viewModel.uiState.value is WeatherUiState.Error)
    }

    @Test
    fun selectingNewCity_addsItToSavedListAndReloads() {
        val viewModel = WeatherViewModel(repository)
        viewModel.selectCity(AustralianCities.Perth)
        assertEquals(AustralianCities.Perth, viewModel.selectedCity.value)
        assertTrue(viewModel.savedCities.value.contains(AustralianCities.Perth))
        assertEquals(AustralianCities.Perth, repository.requestedCities.last())
    }

    @Test
    fun updateSettings_changesOnlyThatSetting() {
        val viewModel = WeatherViewModel(repository)
        viewModel.updateSettings { it.copy(themeMode = ThemeMode.DARK) }
        assertEquals(ThemeMode.DARK, viewModel.settings.value.themeMode)
        assertTrue(viewModel.settings.value.showSunProtection)
    }

    @Test
    fun search_waitsForDebounceBeforeCallingApi() = runTest {
        val viewModel = WeatherViewModel(repository)
        viewModel.onSearchQueryChange("Mag")
        assertTrue(viewModel.search.value.results.isEmpty())
        advanceTimeBy(WeatherViewModel.SEARCH_DEBOUNCE_MS + 1)
        assertEquals("Magnetic Island", viewModel.search.value.results.single().name)
    }
}