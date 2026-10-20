package au.edu.jcu.assessment.utilityapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import au.edu.jcu.assessment.utilityapp.ui.theme.Poppins

/**
 * The big liquid-chrome temperature. Three stacked text layers create the effect:
 * a blurred shadow, a metallic gradient fill, and a thin highlight outline.
 * Light theme uses black chrome; dark theme uses bright silver (as in the reference design).
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun ChromeTemperature(
    value: Int,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 104.sp,
) {
    val fill = if (darkTheme) {
        Brush.verticalGradient(
            0f to Color(0xFFFFFFFF), 0.32f to Color(0xFFC9CED6), 0.5f to Color(0xFF6A717C),
            0.6f to Color(0xFFEDEFF2), 0.82f to Color(0xFFAEB4BD), 1f to Color(0xFF5E646E),
        )
    } else {
        Brush.verticalGradient(
            0f to Color(0xFF5B616B), 0.28f to Color(0xFF15171B), 0.48f to Color(0xFFB9BEC6),
            0.58f to Color(0xFF2A2D33), 0.8f to Color(0xFF0C0D10), 1f to Color(0xFF6B717B),
        )
    }
    val rim = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = if (darkTheme) 0.95f else 0.8f),
            Color.White.copy(alpha = 0.08f),
            Color.White.copy(alpha = 0.7f),
        )
    )
    val shadow = if (darkTheme) Color.Black.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.22f)
    val base = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = fontSize,
        lineHeight = fontSize,
        letterSpacing = (-4).sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false),
    )
    val digits = value.toString()
    val ringSize = with(LocalDensity.current) { (fontSize * 0.22f).toDp() }

    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = "$value degrees" },
        verticalAlignment = Alignment.Top,
    ) {
        Box {
            Text(digits, style = base.copy(color = shadow), modifier = Modifier.offset(x = 2.dp, y = 6.dp).blur(8.dp))
            Text(digits, style = base.copy(brush = fill))
            Text(digits, style = base.copy(brush = rim, drawStyle = Stroke(width = 2.5f)))
        }
        ChromeDegreeRing(darkTheme, Modifier.padding(start = 4.dp, top = 14.dp).size(ringSize))
    }
}

@Composable
private fun ChromeDegreeRing(darkTheme: Boolean, modifier: Modifier = Modifier) {
    val light = if (darkTheme) Color.White else Color(0xFFB9BEC6)
    val dark = if (darkTheme) Color(0xFF7B828C) else Color(0xFF111215)
    Canvas(modifier) {
        val strokeWidth = size.minDimension * 0.26f
        drawCircle(
            brush = Brush.sweepGradient(listOf(light, dark, light, dark, light)),
            radius = size.minDimension / 2f - strokeWidth / 2f,
            style = Stroke(width = strokeWidth),
        )
    }
}