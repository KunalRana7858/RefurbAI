package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LocalThemeIsDark = staticCompositionLocalOf { false }

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8), // Bright sky azure
    onPrimary = Color(0xFF0B0F19),
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = Color(0xFF818CF8), // Bright indigo
    onSecondary = Color(0xFF0B0F19),
    secondaryContainer = Color(0xFF3730A3),
    onSecondaryContainer = Color(0xFFEEF2FF),
    tertiary = Color(0xFF34D399), // Emerald
    onTertiary = Color(0xFF0B0F19),
    tertiaryContainer = Color(0xFF065F46),
    onTertiaryContainer = Color(0xFFECFDF5),
    background = Color(0xFF0B0F19),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF0B0F19)
)

private val LightColorScheme = lightColorScheme(
    primary = AzureBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = AzureBlue,
    secondary = ElectricIndigo,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEF2FF),
    onSecondaryContainer = ElectricIndigo,
    tertiary = EmeraldPass,
    onTertiary = Color.White,
    tertiaryContainer = EmeraldPassBg,
    onTertiaryContainer = EmeraldPass,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = LightSurface,
    onSurface = TextPrimary,
    surfaceVariant = LightCardSubtle,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    error = CrimsonFail,
    onError = Color.White
)

@Composable
fun PhoneKhojTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = (if (darkTheme) Color(0xFF0B0F19) else LightBackground).toArgb()
                it.navigationBarColor = (if (darkTheme) Color(0xFF111827) else Color.White).toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }

    CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

