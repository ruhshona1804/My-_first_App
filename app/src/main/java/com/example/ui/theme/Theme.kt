package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NuviaPrimaryDark,
    onPrimary = NuviaCard,
    primaryContainer = NuviaPrimaryLight,
    onPrimaryContainer = NuviaPrimaryDark,
    secondary = NuviaSecondaryDark,
    onSecondary = NuviaCard,
    secondaryContainer = NuviaSecondary,
    onSecondaryContainer = NuviaTextPrimary,
    tertiary = NuviaAccentDark,
    onTertiary = NuviaCard,
    tertiaryContainer = NuviaAccent,
    onTertiaryContainer = NuviaTextPrimary,
    background = NuviaBackground,
    onBackground = NuviaTextPrimary,
    surface = NuviaCard,
    onSurface = NuviaTextPrimary,
    surfaceVariant = NuviaCardSubtle,
    onSurfaceVariant = NuviaTextSecondary,
    outline = NuviaPrimaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = NuviaPrimary,
    onPrimary = NuviaDarkBackground,
    primaryContainer = NuviaDarkCardSubtle,
    onPrimaryContainer = NuviaPrimaryLight,
    secondary = NuviaSecondary,
    onSecondary = NuviaDarkBackground,
    secondaryContainer = NuviaDarkCard,
    onSecondaryContainer = NuviaDarkTextPrimary,
    tertiary = NuviaAccent,
    onTertiary = NuviaDarkBackground,
    tertiaryContainer = NuviaDarkCard,
    onTertiaryContainer = NuviaDarkTextPrimary,
    background = NuviaDarkBackground,
    onBackground = NuviaDarkTextPrimary,
    surface = NuviaDarkCard,
    onSurface = NuviaDarkTextPrimary,
    surfaceVariant = NuviaDarkCardSubtle,
    onSurfaceVariant = NuviaDarkTextSecondary,
    outline = NuviaDarkCardSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
