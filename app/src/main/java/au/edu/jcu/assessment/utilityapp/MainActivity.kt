package au.edu.jcu.assessment.utilityapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.jcu.assessment.utilityapp.model.ThemeMode
import au.edu.jcu.assessment.utilityapp.screen.SettingsScreen
import au.edu.jcu.assessment.utilityapp.screen.UtilityScreen
import au.edu.jcu.assessment.utilityapp.ui.components.SunGlyph
import au.edu.jcu.assessment.utilityapp.ui.theme.SoutherlyTheme
import au.edu.jcu.assessment.utilityapp.viewmodel.WeatherUiState
import au.edu.jcu.assessment.utilityapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // The theme is applied inside UtilityApp because it depends on the user's Theme setting.
            UtilityApp()
        }
    }
}

/** The two destinations of the starter template's bottom navigation. */
enum class AppTab(val label: String) {
    WEATHER("Weather"),
    SETTINGS("Settings"),
}

/**
 * Main scaffold with bottom navigation (from the starter template).
 * One ViewModel is shared by both screens so a change in Settings shows up on the
 * main screen straight away.
 */
@Composable
fun UtilityApp(
    viewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val city by viewModel.selectedCity.collectAsStateWithLifecycle()
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val search by viewModel.search.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableStateOf(AppTab.WEATHER) }

    // Remember whether it was last day or night so "Sunset" mode doesn't flash while loading.
    var lastIsDay by remember { mutableStateOf(true) }
    LaunchedEffect(uiState) {
        (uiState as? WeatherUiState.Success)?.let { lastIsDay = it.forecast.current.isDay }
    }
    val darkTheme = when (settings.themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SUNSET -> !lastIsDay
    }

    SoutherlyTheme(darkTheme = darkTheme) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.background, tonalElevation = 0.dp) {
                    AppTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = {
                                when (tab) {
                                    AppTab.WEATHER -> SunGlyph(size = 24.dp)
                                    AppTab.SETTINGS -> Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(24.dp))
                                }
                            },
                            label = { Text(tab.label) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            Crossfade(targetState = currentTab, label = "tab") { tab ->
                when (tab) {
                    AppTab.WEATHER -> UtilityScreen(
                        uiState = uiState,
                        city = city,
                        savedCities = savedCities,
                        settings = settings,
                        isRefreshing = isRefreshing,
                        darkTheme = darkTheme,
                        onRefresh = viewModel::refresh,
                        onRetry = viewModel::retry,
                        onCitySelected = viewModel::selectCity,
                        onAddCity = { currentTab = AppTab.SETTINGS },
                        modifier = Modifier.padding(innerPadding),
                    )
                    AppTab.SETTINGS -> SettingsScreen(
                        settings = settings,
                        selectedCity = city,
                        search = search,
                        onSettingsChange = viewModel::updateSettings,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onCityPicked = { picked ->
                            viewModel.selectCity(picked)
                            currentTab = AppTab.WEATHER // jump back so the user sees the result
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}