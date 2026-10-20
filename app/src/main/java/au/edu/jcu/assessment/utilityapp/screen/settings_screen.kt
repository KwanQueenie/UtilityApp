package au.edu.jcu.assessment.utilityapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.model.AppSettings
import au.edu.jcu.assessment.utilityapp.model.AustralianCities
import au.edu.jcu.assessment.utilityapp.model.City
import au.edu.jcu.assessment.utilityapp.model.HeadlineTemperature
import au.edu.jcu.assessment.utilityapp.model.ThemeMode
import au.edu.jcu.assessment.utilityapp.model.WindUnit
import au.edu.jcu.assessment.utilityapp.ui.theme.SoutherlyTheme
import au.edu.jcu.assessment.utilityapp.viewmodel.CitySearchState

/**
 * Settings that change what the main screen shows. Like UtilityScreen it is stateless –
 * the ViewModel owns the values, so changes appear on the main screen immediately.
 */
@Composable
fun SettingsScreen(
    settings: AppSettings,
    selectedCity: City,
    search: CitySearchState,
    onSettingsChange: ((AppSettings) -> AppSettings) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCityPicked: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        SettingsSection("Location") {
            CitySearchField(search = search, onQueryChange = onSearchQueryChange)
            SearchResults(search = search, onCityPicked = onCityPicked)
            Text("Quick picks", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            QuickPicks(selectedCity = selectedCity, onCityPicked = onCityPicked)
        }

        SettingsSection("Appearance") {
            ChoiceRow(
                options = ThemeMode.entries,
                selected = settings.themeMode,
                label = { it.label },
                onSelect = { mode -> onSettingsChange { it.copy(themeMode = mode) } },
            )
            Hint("Sunset switches to the dark look after sunset in the city you are viewing.")
        }

        SettingsSection("Big number shows") {
            ChoiceRow(
                options = HeadlineTemperature.entries,
                selected = settings.headline,
                label = { it.label },
                onSelect = { choice -> onSettingsChange { it.copy(headline = choice) } },
            )
            Hint("\"Feels like\" includes humidity and wind – handy in tropical summers.")
        }

        SettingsSection("Wind speed") {
            ChoiceRow(
                options = WindUnit.entries,
                selected = settings.windUnit,
                label = { it.label },
                onSelect = { unit -> onSettingsChange { it.copy(windUnit = unit) } },
            )
            Hint("Knots suit boating and fishing.")
        }

        SettingsSection("Sun safety") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = settings.showSunProtection,
                        role = Role.Switch,
                        onValueChange = { show -> onSettingsChange { it.copy(showSunProtection = show) } },
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Show sun protection times", style = MaterialTheme.typography.bodyLarge)
                    Hint("When the UV index is 3 or more, cover up with a hat, sunscreen and sunnies.")
                }
                Spacer(Modifier.width(12.dp))
                Switch(checked = settings.showSunProtection, onCheckedChange = null)
            }
        }

        Hint("Settings reset when the app is closed. Weather data by Open-Meteo.com (CC BY 4.0).")
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
private fun Hint(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun CitySearchField(search: CitySearchState, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = search.query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search any Australian town") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (search.isSearching) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else if (search.query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    )
}

@Composable
private fun SearchResults(search: CitySearchState, onCityPicked: (City) -> Unit) {
    search.error?.let { Hint(it) }
    if (search.results.isEmpty()) return
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(Modifier.padding(vertical = 6.dp)) {
            search.results.take(MAX_RESULTS).forEach { city ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCityPicked(city) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(city.label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickPicks(selectedCity: City, onCityPicked: (City) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AustralianCities.all.forEach { city ->
            FilterChip(
                selected = city.isSameAs(selectedCity),
                onClick = { onCityPicked(city) },
                label = { Text(city.name) },
            )
        }
    }
}

/** A Material 3 segmented button row for picking one of a few options. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> ChoiceRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelect(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
            ) {
                Text(label(option), maxLines = 1)
            }
        }
    }
}

private const val MAX_RESULTS = 8

@Preview(showBackground = true, heightDp = 1100)
@Composable
private fun SettingsScreenPreview() {
    SoutherlyTheme(darkTheme = false) {
        Surface {
            SettingsScreen(
                settings = AppSettings(),
                selectedCity = AustralianCities.Townsville,
                search = CitySearchState(
                    query = "Mag",
                    results = listOf(City("Magnetic Island", "QLD", -19.15, 146.85)),
                ),
                onSettingsChange = {},
                onSearchQueryChange = {},
                onCityPicked = {},
            )
        }
    }
}