package au.edu.jcu.assessment.utilityapp.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import au.edu.jcu.assessment.utilityapp.model.IllustrationKind
import au.edu.jcu.assessment.utilityapp.ui.theme.GlowBlue
import au.edu.jcu.assessment.utilityapp.ui.theme.StarMint
import au.edu.jcu.assessment.utilityapp.ui.theme.StormYellow
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * The 3D "clay sphere" weather illustrations are drawn entirely in code with Canvas,
 * so the app ships no image assets and every condition can be animated.
 * All coordinates are on a 200 × 200 design grid and scaled by `u` to the real size.
 */

private data class Sphere(val x: Float, val y: Float, val r: Float)

/** Back row first, then front row, so front spheres overlap the ones behind them. */
private val CloudSpheres = listOf(
    Sphere(66f, 74f, 30f), Sphere(104f, 58f, 40f), Sphere(142f, 76f, 30f),
    Sphere(42f, 106f, 30f), Sphere(80f, 98f, 40f), Sphere(124f, 100f, 38f), Sphere(160f, 108f, 28f),
)

private data class Drop(val x: Float, val y: Float, val r: Float, val phase: Float)

private val DrizzleDrops = listOf(Drop(76f, 162f, 9f, 0f), Drop(126f, 170f, 10f, 0.5f))
private val RainDrops = listOf(
    Drop(62f, 156f, 9f, 0f), Drop(100f, 164f, 8.5f, 0.35f), Drop(138f, 156f, 9f, 0.7f),
    Drop(80f, 192f, 7.5f, 0.5f), Drop(120f, 194f, 7.5f, 0.15f),
)
private val HeavyDrops = listOf(
    Drop(56f, 152f, 8f, 0f), Drop(86f, 156f, 7.5f, 0.4f), Drop(116f, 152f, 8f, 0.2f), Drop(146f, 156f, 7.5f, 0.6f),
    Drop(70f, 186f, 7.5f, 0.3f), Drop(100f, 190f, 8f, 0.8f), Drop(130f, 186f, 7.5f, 0.5f),
)
private val StormDrops = listOf(Drop(54f, 168f, 11f, 0f), Drop(150f, 160f, 10f, 0.5f))

private val StarSpots = listOf(
    Triple(36f, 50f, 7f), Triple(66f, 28f, 5f), Triple(162f, 28f, 6f),
    Triple(186f, 70f, 4f), Triple(26f, 104f, 4f), Triple(118f, 16f, 4f),
)

private val Flakes = listOf(
    Triple(62f, 156f, 11f), Triple(104f, 150f, 9f), Triple(146f, 158f, 11f),
    Triple(82f, 186f, 8f), Triple(126f, 188f, 9f),
)

private val IceBlue = Color(0xFF7FDBFF)
private val FogGrey = Color(0xFF9AA3B2)

/**
 * Large animated weather drawing used on the main screen (and, un-animated, in the
 * highlighted "Now" pill). Cloud underglow colour changes with the weather type.
 */
@Composable
fun WeatherIllustration(
    kind: IllustrationKind,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    animated: Boolean = true,
) {
    val transition = rememberInfiniteTransition(label = "weather-illustration")
    val floatAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float",
    )
    val fallAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "fall",
    )
    val float = if (animated) floatAnim else 0.5f
    val fall = if (animated) fallAnim else 0.35f
    val glow = glowColorFor(kind, darkTheme)

    Canvas(modifier.aspectRatio(1f)) {
        val u = size.minDimension / 200f
        val bob = (float - 0.5f) * 8f * u
        when (kind) {
            IllustrationKind.SUN -> drawSun(Offset(100f * u, 100f * u + bob), 52f * u)
            IllustrationKind.MOON -> {
                drawStars(u, float)
                drawMoon(Offset(104f * u, 100f * u + bob), 56f * u)
            }
            IllustrationKind.SUN_CLOUD -> {
                drawSun(Offset(132f * u, 64f * u + bob * 0.5f), 40f * u)
                drawCloud(u, Offset(-8f * u, 26f * u + bob), 0.8f, glow)
            }
            IllustrationKind.MOON_CLOUD -> {
                drawStars(u, float)
                drawMoon(Offset(136f * u, 58f * u + bob * 0.5f), 40f * u)
                drawCloud(u, Offset(-8f * u, 28f * u + bob), 0.8f, glow)
            }
            IllustrationKind.CLOUD -> drawCloud(u, Offset(0f, 12f * u + bob), 1f, glow)
            IllustrationKind.FOG -> {
                drawCloud(u, Offset(0f, -8f * u + bob), 0.9f, glow)
                drawFog(u, float)
            }
            IllustrationKind.DRIZZLE -> {
                drawCloud(u, Offset(0f, -10f * u + bob), 0.9f, glow)
                drawDrops(u, DrizzleDrops, fall)
            }
            IllustrationKind.RAIN -> {
                drawCloud(u, Offset(0f, -12f * u + bob), 0.9f, glow)
                drawDrops(u, RainDrops, fall)
            }
            IllustrationKind.NIGHT_RAIN -> {
                drawStars(u, float)
                drawMoon(Offset(142f * u, 50f * u + bob * 0.5f), 36f * u)
                drawCloud(u, Offset(-6f * u, -4f * u + bob), 0.84f, glow)
                drawDrops(u, DrizzleDrops, fall)
            }
            IllustrationKind.HEAVY_RAIN -> {
                drawCloud(u, Offset(0f, -14f * u + bob), 0.9f, glow)
                drawDrops(u, HeavyDrops, fall, tiltDegrees = 18f)
            }
            IllustrationKind.SNOW -> {
                drawCloud(u, Offset(0f, -12f * u + bob), 0.9f, glow)
                drawSnow(u, fall)
            }
            IllustrationKind.STORM -> {
                drawBolt(u, Offset(0f, bob * 0.4f))
                drawCloud(u, Offset(0f, -10f * u + bob), 0.9f, glow)
                drawDrops(u, StormDrops, fall)
            }
        }
    }
}

private fun glowColorFor(kind: IllustrationKind, darkTheme: Boolean): Color = when (kind) {
    IllustrationKind.STORM -> StormYellow
    IllustrationKind.SUN_CLOUD -> Color(0xFFFFC94D)
    IllustrationKind.SNOW -> Color(0xFF8FDFFF)
    IllustrationKind.CLOUD, IllustrationKind.FOG -> if (darkTheme) GlowBlue else Color(0xFFAEB8CC)
    else -> GlowBlue
}

private fun DrawScope.drawCloud(u: Float, offset: Offset, scale: Float, glow: Color) {
    val spheres = CloudSpheres.map { s ->
        val center = Offset(
            (100f + (s.x - 100f) * scale) * u + offset.x,
            (100f + (s.y - 100f) * scale) * u + offset.y,
        )
        center to s.r * scale * u
    }

    // Soft coloured halo underneath the whole cloud.
    val bottom = spheres.maxOf { (c, r) -> c.y + r }
    val haloCenter = Offset(100f * u + offset.x, bottom - 12f * u)
    val haloRadius = 92f * u * scale
    drawCircle(
        brush = Brush.radialGradient(listOf(glow.copy(alpha = 0.28f), Color.Transparent), haloCenter, haloRadius),
        radius = haloRadius,
        center = haloCenter,
    )

    spheres.forEach { (c, r) ->
        // Matte black sphere lit from the top-left.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF6A7182), Color(0xFF2B2F38), Color(0xFF0F1115)),
                center = c + Offset(-r * 0.35f, -r * 0.45f),
                radius = r * 1.4f,
            ),
            radius = r,
            center = c,
        )
        // Coloured rim light along the bottom of each sphere (yellow for storms, blue for rain).
        clipPath(Path().apply { addOval(Rect(center = c, radius = r)) }) {
            val glowCenter = c + Offset(0f, r * 1.05f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glow.copy(alpha = 0.95f), glow.copy(alpha = 0.35f), Color.Transparent),
                    center = glowCenter,
                    radius = r * 1.05f,
                ),
                radius = r * 1.1f,
                center = glowCenter,
            )
        }
    }
}

private fun DrawScope.drawSun(center: Offset, r: Float) {
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0x80FFC83D), Color.Transparent), center, r * 1.9f),
        radius = r * 1.9f,
        center = center,
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFFF6B8), Color(0xFFFFCF40), Color(0xFFFF9A1F)),
            center = center + Offset(-r * 0.3f, -r * 0.35f),
            radius = r * 1.5f,
        ),
        radius = r,
        center = center,
    )
}

private fun DrawScope.drawMoon(center: Offset, r: Float) {
    drawCircle(
        brush = Brush.radialGradient(listOf(Color(0x5546E0D0), Color.Transparent), center, r * 1.8f),
        radius = r * 1.8f,
        center = center,
    )
    val crescent = Path.combine(
        PathOperation.Difference,
        Path().apply { addOval(Rect(center = center, radius = r)) },
        Path().apply { addOval(Rect(center = center + Offset(-r * 0.45f, -r * 0.32f), radius = r * 0.86f)) },
    )
    drawPath(
        path = crescent,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFF4FF7A), Color(0xFF6CF0C2), Color(0xFF3E8BFF)),
            start = center + Offset(-r * 0.2f, -r),
            end = center + Offset(r, r * 0.6f),
        ),
    )
}

private fun DrawScope.drawStars(u: Float, twinkle: Float) {
    StarSpots.forEachIndexed { index, (x, y, r) ->
        val alpha = if (index % 2 == 0) 0.55f + 0.45f * twinkle else 1f - 0.45f * twinkle
        val c = Offset(x * u, y * u)
        val radius = r * u
        drawCircle(
            brush = Brush.radialGradient(listOf(StarMint.copy(alpha = 0.5f), Color.Transparent), c, radius * 3f),
            radius = radius * 3f,
            center = c,
            alpha = alpha,
        )
        val star = starPath(c, radius)
        drawPath(path = star, color = StarMint, alpha = alpha)
        drawPath(
            path = star,
            color = StarMint,
            alpha = alpha,
            style = Stroke(width = radius * 0.5f, join = StrokeJoin.Round),
        )
    }
}

private fun starPath(center: Offset, r: Float): Path = Path().apply {
    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) r else r * 0.5f
        val angle = -PI / 2 + i * PI / 5
        val x = center.x + (radius * cos(angle)).toFloat()
        val y = center.y + (radius * sin(angle)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

private fun DrawScope.drawDrops(u: Float, drops: List<Drop>, fall: Float, tiltDegrees: Float = 0f) {
    drops.forEach { drop ->
        val t = (fall + drop.phase) % 1f
        val center = Offset(drop.x * u, drop.y * u + t * 8f * u)
        rotate(tiltDegrees, pivot = center) {
            drawDrop(center, drop.r * u, alpha = 1f - t * 0.35f)
        }
    }
}

/** A raindrop with a yellow-to-blue gradient and a soft blue glow, as in the reference art. */
private fun DrawScope.drawDrop(center: Offset, r: Float, alpha: Float) {
    drawCircle(
        brush = Brush.radialGradient(listOf(GlowBlue.copy(alpha = 0.45f), Color.Transparent), center, r * 2.4f),
        radius = r * 2.4f,
        center = center,
        alpha = alpha,
    )
    drawPath(
        path = teardrop(center, r),
        brush = Brush.verticalGradient(
            0f to Color(0xFFFFE070), 0.5f to Color(0xFFC9EFE0), 1f to Color(0xFF3FA9FF),
            startY = center.y - r * 2.4f,
            endY = center.y + r,
        ),
        alpha = alpha,
    )
}

/** Teardrop whose round bottom is centred on [center] with radius [r]; the tip points up. */
internal fun teardrop(center: Offset, r: Float): Path = Path().apply {
    val (cx, cy) = center
    moveTo(cx, cy - r * 2.4f)
    cubicTo(cx + r * 0.35f, cy - r * 1.5f, cx + r, cy - r * 0.6f, cx + r, cy)
    arcTo(Rect(cx - r, cy - r, cx + r, cy + r), 0f, 180f, false)
    cubicTo(cx - r, cy - r * 0.6f, cx - r * 0.35f, cy - r * 1.5f, cx, cy - r * 2.4f)
    close()
}

private fun DrawScope.drawBolt(u: Float, offset: Offset) {
    val points = listOf(
        108f to 104f, 82f to 156f, 102f to 156f, 90f to 198f, 136f to 138f, 114f to 138f, 132f to 104f,
    )
    val bolt = Path().apply {
        points.forEachIndexed { i, (x, y) ->
            val px = x * u + offset.x
            val py = y * u + offset.y
            if (i == 0) moveTo(px, py) else lineTo(px, py)
        }
        close()
    }
    val glowCenter = Offset(106f * u + offset.x, 152f * u + offset.y)
    drawCircle(
        brush = Brush.radialGradient(listOf(StormYellow.copy(alpha = 0.45f), Color.Transparent), glowCenter, 60f * u),
        radius = 60f * u,
        center = glowCenter,
    )
    val boltBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFFFF2A0), StormYellow, Color(0xFFFFB81C)),
        startY = 104f * u + offset.y,
        endY = 198f * u + offset.y,
    )
    drawPath(path = bolt, brush = boltBrush)
    // A same-colour stroke with rounded corners gives the chunky, soft "cartoon" bolt.
    drawPath(
        path = bolt,
        brush = boltBrush,
        style = Stroke(width = 6f * u, join = StrokeJoin.Round, pathEffect = PathEffect.cornerPathEffect(4f * u)),
    )
}

private fun DrawScope.drawSnow(u: Float, fall: Float) {
    Flakes.forEachIndexed { index, (x, y, r) ->
        val t = (fall + index * 0.2f) % 1f
        val c = Offset(x * u, (y + t * 6f) * u)
        val arm = r * u
        drawCircle(
            brush = Brush.radialGradient(listOf(IceBlue.copy(alpha = 0.45f), Color.Transparent), c, arm * 2.2f),
            radius = arm * 2.2f,
            center = c,
        )
        rotate(t * 30f, pivot = c) {
            for (k in 0 until 3) {
                rotate(k * 60f, pivot = c) {
                    drawLine(IceBlue, c - Offset(0f, arm), c + Offset(0f, arm), strokeWidth = 2.6f * u, cap = StrokeCap.Round)
                    for (end in listOf(-1f, 1f)) {
                        val tip = c + Offset(0f, end * arm * 0.6f)
                        val branch = arm * 0.32f
                        drawLine(IceBlue, tip, tip + Offset(branch, end * branch), strokeWidth = 2f * u, cap = StrokeCap.Round)
                        drawLine(IceBlue, tip, tip + Offset(-branch, end * branch), strokeWidth = 2f * u, cap = StrokeCap.Round)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawFog(u: Float, drift: Float) {
    val bands = listOf(Triple(46f, 150f, 108f), Triple(64f, 168f, 92f), Triple(52f, 186f, 76f))
    bands.forEachIndexed { index, (x, y, width) ->
        val shift = (drift - 0.5f) * (if (index % 2 == 0) 10f else -10f) * u
        val startX = x * u + shift
        val endX = (x + width) * u + shift
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(FogGrey.copy(alpha = 0.2f), FogGrey, FogGrey.copy(alpha = 0.2f)),
                startX = startX,
                endX = endX,
            ),
            start = Offset(startX, y * u),
            end = Offset(endX, y * u),
            strokeWidth = 9f * u,
            cap = StrokeCap.Round,
        )
    }
}