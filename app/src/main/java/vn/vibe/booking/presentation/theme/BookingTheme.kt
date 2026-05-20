package vn.vibe.booking.presentation.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BookingColorScheme: ColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),
    onPrimary = Color.White,
    secondary = Color(0xFF06B6D4),
    onSecondary = Color(0xFF001018),
    tertiary = Color(0xFFEC4899),
    onTertiary = Color.White,
    background = Color(0xFF050816),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0B1220),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF111827),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFF43F5E),
    onError = Color.White,
    outline = Color(0xFF334155)
)

@Composable
fun BookingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BookingColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
