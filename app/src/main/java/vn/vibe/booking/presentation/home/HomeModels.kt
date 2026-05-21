package vn.vibe.booking.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class HomeTab(val label: String?, val icon: ImageVector) {
    Home("", Icons.Default.Home),
    Services("", Icons.Default.Construction),
    Bookings("", Icons.Default.BookOnline),
    Console("", Icons.Default.Build),
    Profile("", Icons.Default.Person)
}
