package au.edu.jcu.assessment.utilityapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.model.CurrentConditions
import au.edu.jcu.assessment.utilityapp.model.UvLevel
import au.edu.jcu.assessment.utilityapp.model.WindUnit
import au.edu.jcu.assessment.utilityapp.model.compassDirection
import au.edu.jcu.assessment.utilityapp.ui.theme.uvColor
import kotlin.math.roundToInt

/**
 * The three numbers under the temperature. The reference design showed humidity, pressure
 * and wind; pressure was swapped for UV because UV is what Australians actually act on.
 */
@Composable
fun StatRow(current: CurrentConditions, windUnit: WindUnit, modifier: Modifier = Modifier) {
    val uvLevel = UvLevel.from(current.uvIndex)
    val uv = current.uvIndex.roundToInt()
    val wind = windUnit.fromKmh(current.windSpeedKmh).roundToInt()
    val direction = compassDirection(current.windDirectionDegrees)

    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        StatItem(
            icon = { DropGlyph() },
            value = "${current.rainChance}%",
            label = "Rain",
            description = "Chance of rain ${current.rainChance} percent",
        )
        StatItem(
            icon = { SunGlyph(color = uvColor(uvLevel)) },
            value = "UV $uv",
            label = uvLevel.label,
            description = "UV index $uv, ${uvLevel.label}",
        )
        StatItem(
            icon = { WindGlyph() },
            value = "$wind ${windUnit.label}",
            label = "Wind $direction",
            description = "Wind $wind ${windUnit.label} from the $direction",
        )
    }
}

@Composable
private fun StatItem(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    description: String,
) {
    Column(
        modifier = Modifier.clearAndSetSemantics { contentDescription = description },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(Modifier.width(6.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}