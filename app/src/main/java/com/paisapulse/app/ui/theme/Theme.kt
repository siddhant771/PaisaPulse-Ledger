package com.paisapulse.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = PulseTeal,
    onPrimary = Color.White,
    primaryContainer = PulseMint,
    onPrimaryContainer = Ink,
    secondary = PulseIndigo,
    onSecondary = Color.White,
    tertiary = PulseOrange,
    error = PulseRed,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFEEF2F5),
    outline = OutlineLight,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = MutedInk
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF57D6C4),
    secondary = Color(0xFFB7C6FF),
    tertiary = Color(0xFFFFB766),
    error = Color(0xFFFFB4AB),
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = Color(0xFFE5EDF4),
    onSurface = Color(0xFFE5EDF4),
    onSurfaceVariant = Color(0xFFBEC9D3)
)

@Composable
fun PaisaPulseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            displaySmall = Typography().displaySmall.copy(fontSize = 34.sp, lineHeight = 40.sp, fontWeight = FontWeight.ExtraBold),
            headlineMedium = Typography().headlineMedium.copy(fontSize = 27.sp, lineHeight = 33.sp, fontWeight = FontWeight.ExtraBold),
            titleLarge = Typography().titleLarge.copy(fontSize = 21.sp, lineHeight = 27.sp, fontWeight = FontWeight.ExtraBold),
            titleMedium = Typography().titleMedium.copy(fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold),
            bodyLarge = Typography().bodyLarge.copy(fontSize = 16.sp, lineHeight = 23.sp),
            bodyMedium = Typography().bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp),
            bodySmall = Typography().bodySmall.copy(fontSize = 12.sp, lineHeight = 17.sp),
            labelLarge = Typography().labelLarge.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)
        ),
        content = content
    )
}
