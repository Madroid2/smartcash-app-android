package com.smartcash.app.core.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    onPrimary = White,
    primaryContainer = EmeraldGreenDark,
    onPrimaryContainer = EmeraldGreenLight,
    secondary = GoldAccent,
    onSecondary = DeepNavy,
    secondaryContainer = Color(0xFF3D3000),
    onSecondaryContainer = GoldLight,
    background = DeepNavy,
    onBackground = White,
    surface = NavySurface,
    onSurface = White,
    surfaceVariant = NavyCard,
    onSurfaceVariant = SlateGray,
    error = ErrorRed,
    onError = White,
)

val LocalDarkMode = compositionLocalOf { true }

@Composable
fun SmartCashTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme // SmartCash is dark-first

    CompositionLocalProvider(LocalDarkMode provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SmartCashTypography,
            content = content,
        )
    }
}
