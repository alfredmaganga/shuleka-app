package com.shuleka.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val ShulekaColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondary,
    error = Color(0xFFEF4444),
)

@Composable
fun ShulekaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ShulekaColorScheme,
        content = content
    )
}
