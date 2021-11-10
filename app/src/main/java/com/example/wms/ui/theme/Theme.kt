package com.example.wms.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = primaryColorLight,
    primaryVariant = primaryColorDark,
    secondary = accentColorLight
)

private val LightColorPalette = lightColors(
    primary = primaryColorDark,
    primaryVariant = primaryColorLight,
    secondary = accentColorDark

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun WarehouseMobileSystemTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
@Composable
fun getAccentColor (darkTheme: Boolean = isSystemInDarkTheme()) : Color {
    return if (darkTheme) accentColorLight
    else accentColorDark
}

@Composable
fun getPrimaryColor (darkTheme: Boolean = isSystemInDarkTheme()) : Color {
    return if (darkTheme) primaryColorLight
    else primaryColorDark
}

@Composable
fun getBackgroundColor (darkTheme: Boolean = isSystemInDarkTheme()) : Color {
    return if (darkTheme) Color.Black
    else Color.White
}