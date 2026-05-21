package vn.vibe.booking.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class HomeTab(val label: String, val icon: ImageVector) {
    Home("Trang chủ", Icons.Default.Home),
    Services("Dịch vụ", Icons.Default.Construction),
    Bookings("Lịch hẹn", Icons.Default.BookOnline),
    Profile("", Icons.Default.Person)
}
