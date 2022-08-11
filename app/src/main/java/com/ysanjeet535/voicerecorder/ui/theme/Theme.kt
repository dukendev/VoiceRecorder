package com.ysanjeet535.voicerecorder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = blueLight,
    primaryVariant = blueDark,
    secondary = greyLight,
    background = lightWhite,
    surface = blueLight,
    onPrimary = greyLight,
    onSecondary = blueDark,
    onBackground = blueLight,
    onSurface = greyLight,
)

private val LightColorPalette = lightColors(
    primary = blueLight,
    primaryVariant = blueDark,
    secondary = greyLight,
    background = lightWhite,
    surface = blueLight,
    onPrimary = greyLight,
    onSecondary = blueDark,
    onBackground = blueLight,
    onSurface = greyLight,

    )

@Composable
fun VoiceRecorderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(
        color = Color.Transparent
    )

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