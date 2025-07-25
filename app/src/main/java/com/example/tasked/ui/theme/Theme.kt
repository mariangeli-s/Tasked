package com.example.tasked.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de color claro
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryLight,
    secondary = AccentGreen,
    onSecondary = OnPrimaryLight,
    tertiary = SoftPurple,
    onTertiary = OnPrimaryLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    error = ErrorRed,
    onError = OnError,
    primaryContainer = LightBlue, // Contenedores con un tono más claro del primario
    onPrimaryContainer = DarkBlue, // Texto oscuro sobre el contenedor primario claro
    secondaryContainer = LightGreen, // Contenedores con un tono más claro del secundario
    onSecondaryContainer = DarkGreen, // Texto oscuro sobre el contenedor secundario claro
    tertiaryContainer = LightPurple,
    onTertiaryContainer = DarkPurple
)

// Esquema de color oscuro
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue, // Podemos mantener un azul similar o ligeramente diferente en oscuro
    onPrimary = OnPrimaryDark,
    secondary = AccentGreen,
    onSecondary = OnPrimaryDark,
    tertiary = SoftPurple,
    onTertiary = OnPrimaryDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    error = ErrorRed,
    onError = OnError,
    primaryContainer = DarkBlue, // Contenedores con un tono más oscuro del primario
    onPrimaryContainer = LightBlue,
    secondaryContainer = DarkGreen,
    onSecondaryContainer = LightGreen,
    tertiaryContainer = DarkPurple,
    onTertiaryContainer = LightPurple
)

@Composable
fun TaskedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usamos la tipografía general definida en Type.kt
        content = content
    )
}