package vn.vibe.booking.presentation.technician

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.presentation.home.HomeViewModel

@Composable
fun TechDashboardScreen(
    viewModel: HomeViewModel,
    token: String?,
    onViewAssigned: () -> Unit
) {
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()

    val assignedBookings = bookingsState.items
    val inProgress = assignedBookings.count {
        it.status.equals("IN_PROGRESS", ignoreCase = true) || it.status.contains("ĐANG", ignoreCase = true)
    }
    val confirmed = assignedBookings.count {
        it.status.equals("CONFIRMED", ignoreCase = true)
    }
    val completed = assignedBookings.count {
        it.status.equals("COMPLETED", ignoreCase = true) || it.status.contains("HOÀN THÀNH", ignoreCase = true)
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
            text = "Kỹ thuật viên",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Công việc được giao",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TechStatCard(
                title = "Đang sửa",
                value = inProgress.toString(),
                icon = Icons.Default.Build,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
            TechStatCard(
                title = "Chờ nhận",
                value = confirmed.toString(),
                icon = Icons.Default.DateRange,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
            TechStatCard(
                title = "Hoàn thành",
                value = completed.toString(),
                icon = Icons.Default.Check,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        HorizontalDivider()

        // Quick action
        Card(
            onClick = onViewAssigned,
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
                    Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Xem công việc", fontWeight = FontWeight.Medium)
                    Text(
                        "Cập nhật trạng thái, thêm ghi chú",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (inProgress + confirmed > 0) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text((inProgress + confirmed).toString())
                    }
                }
            }
        }

        // Active jobs list
        if (assignedBookings.any { !it.status.equals("COMPLETED", ignoreCase = true) && !it.status.equals("CANCELLED", ignoreCase = true) }) {
            Text(
                text = "Công việc đang xử lý",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            assignedBookings
                .filter { !it.status.equals("COMPLETED", ignoreCase = true) && !it.status.equals("CANCELLED", ignoreCase = true) }
                .take(5)
                .forEach { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        onClick = onViewAssigned
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(booking.bookingCode, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    booking.preferredDate ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            vn.vibe.booking.presentation.admin.StatusChip(booking.status)
                        }
                    }
                }
        }

        // Reload
        OutlinedButton(
            onClick = { viewModel.loadMyBookings(token) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tải lại")
        }
    }
}

@Composable
fun TechStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.7f)
            )
        }
    }
}
