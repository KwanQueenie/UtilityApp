package au.edu.jcu.assessment.utilityapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.model.AppSettings
import au.edu.jcu.assessment.utilityapp.model.AustralianCities
import au.edu.jcu.assessment.utilityapp.model.City
import au.edu.jcu.assessment.utilityapp.model.Forecast
import au.edu.jcu.assessment.utilityapp.model.HeadlineTemperature
import au.edu.jcu.assessment.utilityapp.model.IllustrationKind
import au.edu.jcu.assessment.utilityapp.model.WindUnit
import au.edu.jcu.assessment.utilityapp.ui.clockLabel
import au.edu.jcu.assessment.utilityapp.ui.components.ChromeTemperature
import au.edu.jcu.assessment.utilityapp.ui.components.ForecastSection
import au.edu.jcu.assessment.utilityapp.ui.components.MenuGlyph
import au.edu.jcu.assessment.utilityapp.ui.components.StatRow
import au.edu.jcu.assessment.utilityapp.ui.components.SunProtectionCard
import au.edu.jcu.assessment.utilityapp.ui.components.WeatherIllustration
import au.edu.jcu.assessment.utilityapp.ui.degrees
import au.edu.jcu.assessment.utilityapp.ui.preview.SampleData
import au.edu.jcu.assessment.utilityapp.ui.theme.SoutherlyTheme
import au.edu.jcu.assessment.utilityapp.viewmodel.WeatherUiState
import kotlin.math.roundToInt

/**
 * Main "at a glance" screen. Stateless: everything comes in as parameters and every
 * user action goes out as a callback, which keeps it easy to preview and test.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityScreen(
    uiState: WeatherUiState,
    city: City,
    savedCities: List<City>,
    settings: AppSettings,
    isRefreshing: Boolean,
    darkTheme: Boolean,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    onCitySelected: (City) -> Unit,
    onAddCity: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            WeatherHeader(
                city = city,
                subtitle = when (uiState) {
                    is WeatherUiState.Success -> headerSubtitle(uiState.forecast)
                    is WeatherUiState.Error -> "Weather unavailable"
                    WeatherUiState.Loading -> "Checking the sky…"
                },
                savedCities = savedCities,
                onCitySelected = onCitySelected,
                onAddCity = onAddCity,
            )
            when (uiState) {
                WeatherUiState.Loading -> LoadingContent(darkTheme)
                is WeatherUiState.Error -> ErrorContent(uiState.message, darkTheme, onRetry)
                is WeatherUiState.Success -> ForecastContent(uiState.forecast, settings, darkTheme)
            }
        }
    }
}

/** e.g. "It's stormy now · 31° / 22°" */
private fun headerSubtitle(forecast: Forecast): String {
    val current = forecast.current
    val range = forecast.today?.let { " · ${it.maxTemperature.degrees()} / ${it.minTemperature.degrees()}" } ?: ""
    return "It's ${current.condition.describe(current.isDay)} now$range"
}

@Composable
private fun WeatherHeader(
    city: City,
    subtitle: String,
    savedCities: List<City>,
    onCitySelected: (City) -> Unit,
    onAddCity: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box {
            IconButton(
                onClick = { menuOpen = true },
                modifier = Modifier.semantics { contentDescription = "Switch city" },
            ) {
                MenuGlyph()
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                savedCities.forEach { saved ->
                    DropdownMenuItem(
                        text = { Text(saved.label) },
                        onClick = {
                            menuOpen = false
                            onCitySelected(saved)
                        },
                        trailingIcon = {
                            if (saved.isSameAs(city)) Icon(Icons.Rounded.Check, contentDescription = "Selected")
                        },
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Add a city…") },
                    leadingIcon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                    onClick = {
                        menuOpen = false
                        onAddCity()
                    },
                )
            }
        }
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(city.name, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        IconButton(onClick = onAddCity) {
            Icon(Icons.Rounded.Add, contentDescription = "Add city")
        }
    }
}

@Composable
private fun ForecastContent(forecast: Forecast, settings: AppSettings, darkTheme: Boolean) {
    val current = forecast.current
    val showFeelsLike = settings.headline == HeadlineTemperature.FEELS_LIKE
    val headline = if (showFeelsLike) current.feelsLike else current.temperature
    val caption = if (showFeelsLike) {
        "Feels like · actual ${current.temperature.degrees()}"
    } else {
        "Feels like ${current.feelsLike.degrees()}"
    }

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        WeatherIllustration(
            kind = current.condition.illustration(current.isDay),
            darkTheme = darkTheme,
            modifier = Modifier.fillMaxWidth(0.56f),
        )
        ChromeTemperature(value = headline.roundToInt(), darkTheme = darkTheme)
        Text(caption, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        StatRow(current = current, windUnit = settings.windUnit)
        if (settings.showSunProtection) {
            Spacer(Modifier.height(14.dp))
            SunProtectionCard(window = forecast.sunProtection, uvMax = forecast.todayUvMax)
        }
        Spacer(Modifier.height(10.dp))
        ForecastSection(hourly = forecast.hourly, daily = forecast.daily, darkTheme = darkTheme)
        Text(
            "Updated ${current.time.toLocalTime().clockLabel()} local time · Weather data by Open-Meteo.com",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun LoadingContent(darkTheme: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WeatherIllustration(IllustrationKind.CLOUD, darkTheme, Modifier.fillMaxWidth(0.5f))
        Text("Fetching the latest forecast…", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ErrorContent(message: String, darkTheme: Boolean, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WeatherIllustration(IllustrationKind.FOG, darkTheme, Modifier.fillMaxWidth(0.5f))
        Text(message, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        FilledTonalButton(onClick = onRetry) { Text("Try again") }
    }
}

// ---------- Previews ----------

@Composable
private fun UtilityScreenPreview(darkTheme: Boolean, settings: AppSettings = AppSettings()) {
    SoutherlyTheme(darkTheme = darkTheme) {
        Surface {
            UtilityScreen(
                uiState = WeatherUiState.Success(SampleData.forecast),
                city = AustralianCities.Townsville,
                savedCities = AustralianCities.defaultSaved,
                settings = settings,
                isRefreshing = false,
                darkTheme = darkTheme,
                onRefresh = {}, onRetry = {}, onCitySelected = {}, onAddCity = {},
            )
        }
    }
}

@Preview(name = "Light", showBackground = true, heightDp = 900)
@Composable
private fun LightPreview() = UtilityScreenPreview(darkTheme = false)

@Preview(name = "Dark", showBackground = true, heightDp = 900)
@Composable
private fun DarkPreview() = UtilityScreenPreview(darkTheme = true)

@Preview(name = "Feels like, knots", showBackground = true, heightDp = 900)
@Composable
private fun SettingsAppliedPreview() = UtilityScreenPreview(
    darkTheme = false,
    settings = AppSettings(
        headline = HeadlineTemperature.FEELS_LIKE,
        windUnit = WindUnit.KNOTS,
    ),
)

@Preview(name = "Error", showBackground = true)
@Composable
private fun ErrorPreview() {
    SoutherlyTheme(darkTheme = false) {
        Surface {
            ErrorContent("Can't reach the weather service. Check your internet connection.", false) {}
        }
    }
}