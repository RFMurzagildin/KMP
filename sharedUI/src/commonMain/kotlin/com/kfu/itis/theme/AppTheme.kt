package com.kfu.itis.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Indigo600,
    onPrimary = White,
    primaryContainer = Indigo50,
    onPrimaryContainer = Indigo800,
    secondary = Violet600,
    onSecondary = White,
    secondaryContainer = Indigo100,
    onSecondaryContainer = Indigo700,
    background = Gray50,
    onBackground = Gray900,
    surface = White,
    onSurface = Gray800,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    error = Red600,
    onError = White,
    outline = Gray200,
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo400,
    onPrimary = Indigo950,
    primaryContainer = Indigo800,
    onPrimaryContainer = Indigo100,
    secondary = Violet400,
    onSecondary = Indigo950,
    secondaryContainer = Indigo700,
    onSecondaryContainer = Indigo100,
    background = Slate900,
    onBackground = Slate100,
    surface = Slate800,
    onSurface = Slate200,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray200,
    error = Red400,
    onError = Black,
    outline = Gray700,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
