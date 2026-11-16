package com.Qempibo9.Pebfik.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = SurfaceHigh,
    onPrimaryContainer = TextPrimary,
    secondary = Secondary,
    onSecondary = TextOnAccent,
    secondaryContainer = SurfaceLow,
    onSecondaryContainer = Secondary,
    tertiary = Accent,
    onTertiary = TextOnAccent,
    tertiaryContainer = AccentDeep,
    onTertiaryContainer = TextOnAccent,
    background = BgBase,
    onBackground = TextPrimary,
    surface = SurfaceLow,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = TextSecondary,
    outline = Secondary,
    outlineVariant = SurfaceHigh,
    error = Danger,
    onError = TextPrimary,
    scrim = BgDeep
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content
    )
}
