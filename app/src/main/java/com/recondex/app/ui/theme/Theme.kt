package com.recondex.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = DuoGreen,
    onPrimary = SurfaceLight,
    secondary = DuoBlue,
    onSecondary = SurfaceLight,
    tertiary = DuoYellow,
    onTertiary = TextPrimary,
    error = DuoRed,
    onError = SurfaceLight,
    background = BgLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

private val DarkColors = darkColorScheme(
    primary = DuoGreen,
    onPrimary = SurfaceDark,
    secondary = DuoBlue,
    onSecondary = SurfaceDark,
    tertiary = DuoYellow,
    onTertiary = TextPrimaryDark,
    error = DuoRed,
    onError = SurfaceDark,
    background = BgDark,
    surface = SurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
)

@Composable
fun RecoNDexTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = RecoNDexTypography,
        content = content
    )
}
