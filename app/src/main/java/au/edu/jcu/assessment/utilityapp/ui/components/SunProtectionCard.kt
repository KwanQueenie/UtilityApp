package au.edu.jcu.assessment.utilityapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.model.SunProtectionWindow
import au.edu.jcu.assessment.utilityapp.model.UvLevel
import au.edu.jcu.assessment.utilityapp.ui.clockLabel
import au.edu.jcu.assessment.utilityapp.ui.theme.uvColor
import kotlin.math.roundToInt

/**
 * Australia-specific: shows when sun protection is needed today (UV index 3+),
 * the same rule used by the Bureau of Meteorology and Cancer Council's sun protection times.
 */
@Composable
fun SunProtectionCard(window: SunProtectionWindow?, uvMax: Double, modifier: Modifier = Modifier) {
    val level = UvLevel.from(uvMax)
    val accent = uvColor(level)
    val peak = uvMax.roundToInt()

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
    ) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(accent.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                SunGlyph(color = accent, size = 22.dp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                if (window != null) {
                    Text(
                        "Sun protection ${window.start.clockLabel()} – ${window.end.clockLabel()}",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        "UV peaks at $peak (${level.label}). Hat, sunscreen and sunnies.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text("No sun protection needed today", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "UV stays low – the maximum today is $peak.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}