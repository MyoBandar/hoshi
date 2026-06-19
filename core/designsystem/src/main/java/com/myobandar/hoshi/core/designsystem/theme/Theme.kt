package com.myobandar.hoshi.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = HoshiPurpleLight,
    onPrimary = HoshiDarkBackground,
    primaryContainer = HoshiPurpleDark,
    onPrimaryContainer = HoshiDarkText,
    secondary = HoshiCyan,
    onSecondary = HoshiDarkBackground,
    secondaryContainer = ColorSchemeTokens.CyanContainerDark,
    onSecondaryContainer = HoshiDarkText,
    tertiary = HoshiAmber,
    onTertiary = HoshiDarkBackground,
    tertiaryContainer = ColorSchemeTokens.AmberContainerDark,
    onTertiaryContainer = HoshiDarkText,
    background = HoshiDarkBackground,
    onBackground = HoshiDarkText,
    surface = HoshiDarkSurface,
    onSurface = HoshiDarkText,
    surfaceVariant = HoshiDarkSurfaceHigh,
    onSurfaceVariant = HoshiDarkMutedText,
    outline = HoshiDarkOutline,
    outlineVariant = ColorSchemeTokens.OutlineVariantDark,
    inverseSurface = HoshiDarkText,
    inverseOnSurface = HoshiDarkBackground,
    error = ColorSchemeTokens.ErrorDark,
    onError = HoshiDarkBackground
)

private val LightColorScheme = lightColorScheme(
    primary = HoshiPurple,
    onPrimary = HoshiLightSurface,
    primaryContainer = HoshiLightSurfaceHigh,
    onPrimaryContainer = HoshiLightText,
    secondary = HoshiCyan,
    onSecondary = HoshiLightSurface,
    secondaryContainer = ColorSchemeTokens.CyanContainerLight,
    onSecondaryContainer = HoshiLightText,
    tertiary = HoshiAmber,
    onTertiary = HoshiLightText,
    tertiaryContainer = ColorSchemeTokens.AmberContainerLight,
    onTertiaryContainer = HoshiLightText,
    background = HoshiLightBackground,
    onBackground = HoshiLightText,
    surface = HoshiLightSurface,
    onSurface = HoshiLightText,
    surfaceVariant = HoshiLightSurfaceHigh,
    onSurfaceVariant = HoshiLightMutedText,
    outline = HoshiLightOutline,
    outlineVariant = ColorSchemeTokens.OutlineVariantLight,
    inverseSurface = HoshiLightText,
    inverseOnSurface = HoshiLightSurface,
    error = ColorSchemeTokens.ErrorLight,
    onError = HoshiLightSurface
)

private object ColorSchemeTokens {
    val AmberContainerDark = Color(0xFF4B443B)
    val AmberContainerLight = Color(0xFFFFE4A3)
    val CyanContainerDark = Color(0xFF173346)
    val CyanContainerLight = Color(0xFFD8EEFF)
    val OutlineVariantDark = Color(0xFF252B35)
    val OutlineVariantLight = Color(0xFFD9D6E2)
    val ErrorDark = Color(0xFFFFB4AB)
    val ErrorLight = Color(0xFFBA1A1A)
}

@Composable
fun HoshiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        val context = LocalContext.current
        SideEffect {
            val window = (context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
