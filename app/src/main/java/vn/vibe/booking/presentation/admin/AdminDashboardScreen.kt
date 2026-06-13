package vn.vibe.booking.presentation.admin

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.presentation.home.HomeViewModel
import vn.vibe.booking.domain.model.UiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: HomeViewModel,
    token: String?,
    onManageBookings: () -> Unit,
    onManageUsers: () -> Unit,
    onManageRevenue: () -> Unit,
    onManageInventory: () -> Unit
) {
    val dashboardState by viewModel.dashboardState.collectAsStateWithLifecycle()
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
    val usersState by viewModel.usersState.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        if (token != null) {
            viewModel.loadDashboardOverview(token)
            viewModel.loadBookings(token)
            viewModel.loadUsers(token)
        }
    }

    val overview = (dashboardState as? UiState.Success)?.data

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    val totalRevenueStr = overview?.totalRevenue?.let { currencyFormatter.format(it) } ?: "0 đ"
    val totalBookingsStr = overview?.totalBookings?.toString() ?: "0"
    val totalUsersStr = if (overview != null && overview.totalUsers > 0) {
        overview.totalUsers.toString()
    } else {
        usersState.total.toString()
    }
    val totalInventoryStr = overview?.totalInventoryItems?.toString() ?: "0"

    val pendingBookings = bookingsState.items.count { 
        it.status.equals("PENDING", ignoreCase = true) || it.status.contains("CHỜ", ignoreCase = true) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Admin Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Tổng quan hệ thống",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (dashboardState is UiState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            // Stats Grid 2x2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Doanh thu",
                    value = totalRevenueStr,
                    icon = Icons.Default.Star,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f).clickable { onManageRevenue() }
                )
                StatCard(
                    title = "Bookings",
                    value = totalBookingsStr,
                    icon = Icons.Default.DateRange,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.weight(1f).clickable { onManageBookings() }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Users",
                    value = totalUsersStr,
                    icon = Icons.Default.Person,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f).clickable { onManageUsers() }
                )
                StatCard(
                    title = "Quản lý kho",
                    value = totalInventoryStr,
                    icon = Icons.Default.ShoppingCart,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f).clickable { onManageInventory() }
                )
            }
        }

        HorizontalDivider()

        // Quick Actions
        Text(
            text = "Quản lý nhanh",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            onClick = onManageBookings,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Quản lý Booking", fontWeight = FontWeight.Medium)
                    Text(
                        "Xác nhận, phân công, cập nhật trạng thái",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (pendingBookings > 0) {
                    Badge(containerColor = MaterialTheme.colorScheme.error) {
                        Text(pendingBookings.toString())
                    }
                }
            }
        }

        Card(
            onClick = onManageUsers,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Quản lý User", fontWeight = FontWeight.Medium)
                    Text(
                        "Thêm, sửa, khóa tài khoản",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Recent Pending Bookings
        if (pendingBookings > 0) {
            HorizontalDivider()
            Text(
                text = "Booking chờ xử lý",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            bookingsState.items
                .filter { it.status.equals("PENDING", ignoreCase = true) || it.status.contains("CHỜ", ignoreCase = true) }
                .take(5)
                .forEach { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(booking.bookingCode, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    booking.preferredDate ?: "Chưa đặt ngày",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            SuggestionChip(
                                onClick = onManageBookings,
                                label = { Text("Xử lý", style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.defaultMinSize(minHeight = 110.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f)
            )
        }
    }
}
