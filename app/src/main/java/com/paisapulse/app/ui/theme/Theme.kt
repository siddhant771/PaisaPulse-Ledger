package com.paisapulse.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = EmeraldGreen,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = EmeraldGreen,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = Color.White
)

@Composable
fun PaisaPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}