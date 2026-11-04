package com.recondex.app.ui.theme

import android.app.Activity
import android.os.Build
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
    primary = NexusIndigo,
    onPrimary = SurfaceLight,
    secondary = MintSuccess,
    tertiary = AmberStreak,
    error = DangerCoral,
    background = BgLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

private val DarkColors = darkColorScheme(
    primary = NexusIndigo,
    onPrimary = SurfaceDark,
    secondary = MintSuccess,
    tertiary = AmberStreak,
    error = DangerCoral,
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
                window.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = RecoNDexTypography,
        content = content
    )
}
