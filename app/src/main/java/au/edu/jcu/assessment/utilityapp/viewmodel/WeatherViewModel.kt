package au.edu.jcu.assessment.utilityapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import au.edu.jcu.assessment.utilityapp.SoutherlyApplication
import au.edu.jcu.assessment.utilityapp.data.WeatherRepository
import au.edu.jcu.assessment.utilityapp.model.AppSettings
import au.edu.jcu.assessment.utilityapp.model.AustralianCities
import au.edu.jcu.assessment.utilityapp.model.City
import au.edu.jcu.assessment.utilityapp.model.Forecast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/** What the main screen should show. */
sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val forecast: Forecast) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

/** State of the city search box on the Settings screen. */
data class CitySearchState(
    val query: String = "",
    val results: List<City> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null,
)

/**
 * Single source of truth for both screens. It survives rotation, owns all business logic,
 * and exposes read-only [StateFlow]s that the Compose UI collects.
 */
class WeatherViewModel(
    private val repository: WeatherRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _selectedCity = MutableStateFlow(AustralianCities.Townsville)
    val selectedCity: StateFlow<City> = _selectedCity.asStateFlow()

    private val _savedCities = MutableStateFlow(AustralianCities.defaultSaved)
    val savedCities: StateFlow<List<City>> = _savedCities.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _search = MutableStateFlow(CitySearchState())
    val search: StateFlow<CitySearchState> = _search.asStateFlow()

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    init {
        loadForecast()
    }

    /** Pull-to-refresh: keeps the current forecast on screen while new data loads. */
    fun refresh() = loadForecast(userInitiated = true)

    /** "Try again" button on the error state. */
    fun retry() {
        _uiState.value = WeatherUiState.Loading
        loadForecast()
    }

    fun selectCity(city: City) {
        if (_savedCities.value.none { it.isSameAs(city) }) {
            _savedCities.update { it + city }
        }
        clearSearch()
        val alreadyShowing = city.isSameAs(_selectedCity.value) && _uiState.value is WeatherUiState.Success
        if (alreadyShowing) return
        _selectedCity.value = city
        _uiState.value = WeatherUiState.Loading
        loadForecast()
    }

    fun updateSettings(change: (AppSettings) -> AppSettings) {
        _settings.update(change)
    }

    /** Debounced so we only call the geocoding API once the user pauses typing. */
    fun onSearchQueryChange(query: String) {
        _search.update { it.copy(query = query, error = null) }
        searchJob?.cancel()
        val trimmed = query.trim()
        if (trimmed.length < MIN_SEARCH_LENGTH) {
            _search.update { it.copy(results = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _search.update { it.copy(isSearching = true) }
            try {
                val results = repository.searchCities(trimmed)
                _search.update {
                    it.copy(
                        results = results,
                        isSearching = false,
                        error = if (results.isEmpty()) "No Australian places match \"$trimmed\"" else null,
                    )
                }
            } catch (e: IOException) {
                _search.update { it.copy(isSearching = false, error = OFFLINE_MESSAGE) }
            } catch (e: HttpException) {
                _search.update { it.copy(isSearching = false, error = "Search failed (error ${e.code()})") }
            } catch (e: SerializationException) {
                _search.update { it.copy(isSearching = false, error = BAD_DATA_MESSAGE) }
            }
        }
    }

    fun clearSearch() {
        searchJob?.cancel()
        _search.value = CitySearchState()
    }

    private fun loadForecast(userInitiated: Boolean = false) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            if (userInitiated) _isRefreshing.value = true
            try {
                _uiState.value = WeatherUiState.Success(repository.getForecast(_selectedCity.value))
            } catch (e: IOException) {
                _uiState.value = WeatherUiState.Error(OFFLINE_MESSAGE)
            } catch (e: HttpException) {
                _uiState.value = WeatherUiState.Error("The weather service returned error ${e.code()}. Please try again.")
            } catch (e: SerializationException) {
                _uiState.value = WeatherUiState.Error(BAD_DATA_MESSAGE)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_MS = 350L
        private const val MIN_SEARCH_LENGTH = 2
        private const val OFFLINE_MESSAGE = "Can't reach the weather service. Check your internet connection."
        private const val BAD_DATA_MESSAGE = "The weather service sent something unexpected. Please try again."

        /** Builds the ViewModel with the repository from the app's DI container. */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as SoutherlyApplication
                WeatherViewModel(application.container.weatherRepository)
            }
        }
    }
}