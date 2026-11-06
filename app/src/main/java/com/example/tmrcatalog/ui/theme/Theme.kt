package com.example.tmrcatalog.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.tmrcatalog.R

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D40),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFFF007F),
    onSecondary = Color.White,
    background = Color.Transparent,
    surface = Color(0xCC121212),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFE0E0E0),
    errorContainer = Color(0xCCB00020),
    onErrorContainer = Color.White
)

@Composable
fun TMRCatalogTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo Neón Global
            Image(
                painter = painterResource(id = R.drawable.fondoneon),
                contentDescription = "Fondo Neón",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Superficie transparente para que el fondo traspase a todas las pantallas
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                content()
            }
        }
    }
}