package com.riotech.rtbusinessbook.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val RtBlue = Color(0xFF1565C0)
val RtYellow = Color(0xFFFFC107)
val IncomeGreen = Color(0xFF2E7D32)
val ExpenseRed = Color(0xFFC62828)

private val Light = lightColorScheme(
    primary = RtBlue, onPrimary = Color.White,
    secondary = RtYellow, onSecondary = Color.Black,
    background = Color(0xFFF5F8FF), surface = Color.White
)
private val Dark = darkColorScheme(
    primary = Color(0xFF90CAF9), onPrimary = Color.Black,
    secondary = RtYellow, onSecondary = Color.Black,
    background = Color(0xFF0D1B2A), surface = Color(0xFF1B263B)
)

@Composable
fun RtTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
