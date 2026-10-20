package au.edu.jcu.assessment.utilityapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import au.edu.jcu.assessment.utilityapp.model.IllustrationKind
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * Thin line icons matching the outline style of the reference design.
 * Drawn on a 24 × 24 grid (like Material icons) and scaled with `s`.
 */

private fun DrawScope.lineStroke(s: Float, width: Float = 1.6f) =
    Stroke(width = width * s, cap = StrokeCap.Round, join = StrokeJoin.Round)

/** Menu icon with a shorter middle bar, as in the reference header. */
@Composable
fun MenuGlyph(modifier: Modifier = Modifier, size: Dp = 22.dp, color: Color = LocalContentColor.current) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension / 24f
        val bars = listOf(6f to 15f, 12f to 9f, 18f to 15f)
        bars.forEach { (y, length) ->
            drawLine(color, Offset(4f * s, y * s), Offset((4f + length) * s, y * s), strokeWidth = 2.2f * s, cap = StrokeCap.Round)
        }
    }
}

@Composable
fun DropGlyph(modifier: Modifier = Modifier, size: Dp = 20.dp, color: Color = LocalContentColor.current) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension / 24f
        drawPath(teardrop(Offset(12f * s, 15f * s), 5f * s), color, style = lineStroke(s, 1.8f))
    }
}

@Composable
fun SunGlyph(modifier: Modifier = Modifier, size: Dp = 20.dp, color: Color = LocalContentColor.current) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension / 24f
        drawSunLines(s, Offset(12f * s, 12f * s), 4.2f, color, rays = 8)
    }
}

@Composable
fun WindGlyph(modifier: Modifier = Modifier, size: Dp = 20.dp, color: Color = LocalContentColor.current) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension / 24f
        val stroke = lineStroke(s, 1.8f)
        val top = Path().apply {
            moveTo(3f * s, 8f * s); lineTo(14f * s, 8f * s)
            cubicTo(17.5f * s, 8f * s, 17.5f * s, 3.5f * s, 14.5f * s, 3.8f * s)
        }
        val bottom = Path().apply {
            moveTo(3f * s, 16f * s); lineTo(12f * s, 16f * s)
            cubicTo(15.5f * s, 16f * s, 15.5f * s, 20.5f * s, 12.5f * s, 20.2f * s)
        }
        drawPath(top, color, style = stroke)
        drawLine(color, Offset(3f * s, 12f * s), Offset(19f * s, 12f * s), strokeWidth = 1.8f * s, cap = StrokeCap.Round)
        drawPath(bottom, color, style = stroke)
    }
}

/**
 * Small outline weather icon for the forecast pills. [background] fills the cloud so that
 * a sun or moon behind it is hidden, giving a clean overlap.
 */
@Composable
fun LineWeatherIcon(
    kind: IllustrationKind,
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    color: Color = LocalContentColor.current,
    background: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension / 24f
        val stroke = lineStroke(s)
        fun p(x: Float, y: Float) = Offset(x * s, y * s)
        fun cloud(dx: Float = 0f, dy: Float = 0f) {
            val outline = cloudOutline(s, dx, dy)
            drawPath(outline, background)
            drawPath(outline, color, style = stroke)
        }
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(color, p(x1, y1), p(x2, y2), strokeWidth = 1.6f * s, cap = StrokeCap.Round)

        when (kind) {
            IllustrationKind.SUN -> drawSunLines(s, p(12f, 12f), 4.2f, color, rays = 8)
            IllustrationKind.MOON -> drawPath(crescentOutline(s, p(12.5f, 12f), 7.5f), color, style = stroke)
            IllustrationKind.SUN_CLOUD -> {
                drawSunLines(s, p(15.5f, 8f), 3f, color, rays = 6)
                cloud(dx = -1.5f, dy = 3f)
            }
            IllustrationKind.MOON_CLOUD -> {
                drawPath(crescentOutline(s, p(16f, 7.5f), 4.5f), color, style = stroke)
                cloud(dx = -1.5f, dy = 3f)
            }
            IllustrationKind.CLOUD -> cloud(dy = 0.5f)
            IllustrationKind.FOG -> {
                cloud(dy = -3f)
                line(5f, 18.5f, 19f, 18.5f)
                line(7f, 21.5f, 17f, 21.5f)
            }
            IllustrationKind.DRIZZLE -> {
                cloud(dy = -3f)
                listOf(8f to 19f, 12f to 21f, 16f to 19f).forEach { (x, y) ->
                    drawCircle(color, radius = 0.95f * s, center = p(x, y))
                }
            }
            IllustrationKind.RAIN, IllustrationKind.NIGHT_RAIN -> {
                cloud(dy = -3f)
                listOf(8.5f, 12.5f, 16.5f).forEach { x -> line(x, 18f, x - 1.2f, 21.5f) }
            }
            IllustrationKind.HEAVY_RAIN -> {
                cloud(dy = -3f)
                listOf(7f, 10.5f, 14f, 17.5f).forEach { x -> line(x, 18f, x - 1.6f, 22.5f) }
            }
            IllustrationKind.SNOW -> {
                cloud(dy = -3f)
                listOf(8f to 19.5f, 12f to 21.5f, 16f to 19.5f).forEach { (x, y) ->
                    line(x - 1.1f, y, x + 1.1f, y)
                    line(x, y - 1.1f, x, y + 1.1f)
                }
            }
            IllustrationKind.STORM -> {
                cloud(dy = -3f)
                val bolt = Path().apply {
                    moveTo(12.8f * s, 15.5f * s); lineTo(10.4f * s, 19.6f * s); lineTo(12.6f * s, 19.6f * s)
                    lineTo(11.3f * s, 23f * s); lineTo(14.6f * s, 18.3f * s); lineTo(12.4f * s, 18.3f * s)
                    lineTo(13.8f * s, 15.5f * s); close()
                }
                drawPath(bolt, color)
            }
        }
    }
}

private fun cloudOutline(s: Float, dx: Float, dy: Float): Path {
    fun circle(x: Float, y: Float, r: Float) =
        Path().apply { addOval(Rect(center = Offset((x + dx) * s, (y + dy) * s), radius = r * s)) }
    val base = Path().apply {
        addRoundRect(
            RoundRect(
                left = (4.5f + dx) * s, top = (13f + dy) * s,
                right = (20.5f + dx) * s, bottom = (18.5f + dy) * s,
                cornerRadius = CornerRadius(2.75f * s),
            )
        )
    }
    var outline = Path.combine(PathOperation.Union, circle(8.5f, 14f, 4f), circle(13f, 11f, 5.5f))
    outline = Path.combine(PathOperation.Union, outline, circle(17.5f, 14.5f, 3.5f))
    return Path.combine(PathOperation.Union, outline, base)
}

private fun crescentOutline(s: Float, center: Offset, r: Float): Path = Path.combine(
    PathOperation.Difference,
    Path().apply { addOval(Rect(center = center, radius = r * s)) },
    Path().apply { addOval(Rect(center = center + Offset(-r * 0.45f * s, -r * 0.35f * s), radius = r * 0.82f * s)) },
)

private fun DrawScope.drawSunLines(s: Float, center: Offset, r: Float, color: Color, rays: Int) {
    drawCircle(color, radius = r * s, center = center, style = lineStroke(s))
    repeat(rays) { i ->
        val angle = i * 2 * PI / rays
        val inner = (r + 2.2f) * s
        val outer = (r + 4f) * s
        drawLine(
            color,
            center + Offset((inner * cos(angle)).toFloat(), (inner * sin(angle)).toFloat()),
            center + Offset((outer * cos(angle)).toFloat(), (outer * sin(angle)).toFloat()),
            strokeWidth = 1.6f * s,
            cap = StrokeCap.Round,
        )
    }
}