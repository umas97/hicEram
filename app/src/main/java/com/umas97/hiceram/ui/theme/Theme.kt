package com.umas97.hiceram.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

import androidx.compose.ui.graphics.Color

private fun darkScheme(accentColor: Color) = darkColorScheme(
    primary = accentColor,
    onPrimary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkSecondary,
    error = ErrorColor
)

private fun lightScheme(accentColor: Color) = lightColorScheme(
    primary = accentColor,
    onPrimary = LightBackground,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightSecondary,
    error = ErrorColor
)

@Composable
fun HicEramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    accentColor: Color = AccentColors[0],
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkScheme(accentColor) else lightScheme(accentColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
