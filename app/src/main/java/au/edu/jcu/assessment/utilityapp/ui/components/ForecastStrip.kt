package au.edu.jcu.assessment.utilityapp.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.model.DayForecast
import au.edu.jcu.assessment.utilityapp.model.HourForecast
import au.edu.jcu.assessment.utilityapp.model.IllustrationKind
import au.edu.jcu.assessment.utilityapp.ui.dayLabel
import au.edu.jcu.assessment.utilityapp.ui.degrees
import au.edu.jcu.assessment.utilityapp.ui.hourLabel
import au.edu.jcu.assessment.utilityapp.ui.theme.RainBlue

/** "Today" hourly pills, switchable to a 7-day view with the "This week ›" link. */
@Composable
fun ForecastSection(
    hourly: List<HourForecast>,
    daily: List<DayForecast>,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    var showWeek by rememberSaveable { mutableStateOf(false) }

    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(if (showWeek) "This week" else "Today", style = MaterialTheme.typography.titleMedium)
            TextButton(
                onClick = { showWeek = !showWeek },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            ) {
                Text(if (showWeek) "Today" else "This week", style = MaterialTheme.typography.labelLarge)
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        AnimatedContent(
            targetState = showWeek,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(150)) },
            label = "forecast-strip",
        ) { week ->
            if (week) DailyStrip(daily, darkTheme) else HourlyStrip(hourly, darkTheme)
        }
    }
}

@Composable
private fun HourlyStrip(hours: List<HourForecast>, darkTheme: Boolean) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 2.dp),
    ) {
        itemsIndexed(hours, key = { _, hour -> hour.time.toString() }) { index, hour ->
            val label = if (index == 0) "Now" else hour.time.hourLabel()
            // Rain chance only appears when it is worth noticing.
            val rain = hour.rainChance.takeIf { it >= RAIN_CHANCE_TO_SHOW }?.let { "$it%" }
            ForecastPill(
                label = label,
                value = hour.temperature.degrees(),
                detail = rain,
                detailColor = RainBlue,
                kind = hour.condition.illustration(hour.isDay),
                highlighted = index == 0,
                darkTheme = darkTheme,
                description = "$label, ${hour.condition.label}, ${hour.temperature.degrees()}" +
                        (rain?.let { ", $it chance of rain" } ?: ""),
            )
        }
    }
}

@Composable
private fun DailyStrip(days: List<DayForecast>, darkTheme: Boolean) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 2.dp),
    ) {
        itemsIndexed(days, key = { _, day -> day.date.toString() }) { index, day ->
            val label = if (index == 0) "Today" else day.date.dayLabel()
            ForecastPill(
                label = label,
                value = day.maxTemperature.degrees(),
                detail = day.minTemperature.degrees(),
                detailColor = MaterialTheme.colorScheme.onSurfaceVariant,
                kind = day.condition.illustration(isDay = true),
                highlighted = index == 0,
                darkTheme = darkTheme,
                description = "$label, ${day.condition.label}, high ${day.maxTemperature.degrees()}, " +
                        "low ${day.minTemperature.degrees()}, ${day.rainChance}% chance of rain",
            )
        }
    }
}

@Composable
private fun ForecastPill(
    label: String,
    value: String,
    detail: String?,
    detailColor: Color,
    kind: IllustrationKind,
    highlighted: Boolean,
    darkTheme: Boolean,
    description: String,
) {
    val pillColor = if (highlighted) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceVariant
    Surface(
        shape = RoundedCornerShape(percent = 50),
        color = pillColor,
        shadowElevation = if (highlighted && !darkTheme) 10.dp else 0.dp,
        modifier = Modifier
            .width(62.dp)
            .height(116.dp)
            .clearAndSetSemantics { contentDescription = description },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            if (highlighted) {
                // The current hour gets the full-colour 3D drawing, as in the reference design.
                WeatherIllustration(kind = kind, darkTheme = darkTheme, modifier = Modifier.size(36.dp), animated = false)
            } else {
                LineWeatherIcon(kind = kind, size = 30.dp, color = MaterialTheme.colorScheme.onSurface, background = pillColor)
            }
            Text(value, style = MaterialTheme.typography.titleSmall)
            Text(detail ?: "", style = MaterialTheme.typography.labelSmall, color = detailColor)
        }
    }
}

private const val RAIN_CHANCE_TO_SHOW = 20