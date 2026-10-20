package au.edu.jcu.assessment.utilityapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = InkLight,
    onPrimary = Paper,
    secondary = RainBlue,
    secondaryContainer = OutlineLight,
    onSecondaryContainer = InkLight,
    background = Paper,
    onBackground = InkLight,
    surface = Paper,
    onSurface = InkLight,
    surfaceVariant = PillLight,
    onSurfaceVariant = MutedLight,
    surfaceContainer = Paper,
    surfaceContainerHigh = Paper,
    outline = OutlineLight,
    outlineVariant = OutlineLight,
)

private val DarkColors = darkColorScheme(
    primary = InkDark,
    onPrimary = Night,
    secondary = RainBlue,
    secondaryContainer = PillDarkSelected,
    onSecondaryContainer = InkDark,
    background = Night,
    onBackground = InkDark,
    surface = Night,
    onSurface = InkDark,
    surfaceVariant = PillDark,
    onSurfaceVariant = MutedDark,
    surfaceContainer = Night,
    surfaceContainerHigh = PillDarkSelected,
    outline = OutlineDark,
    outlineVariant = OutlineDark,
)

/**
 * App theme. Dark mode is decided by the user's Theme setting (see UtilityApp),
 * not only by the system, so the status bar icon colour is updated here too.
 */
@Composable
fun SoutherlyTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = SoutherlyTypography,
        content = content,
    )
}