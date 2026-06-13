package vn.vibe.booking.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.vibe.booking.domain.model.UserInfo
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.BookingSummaryDto
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat

@Composable
fun DashboardScreen(
    userInfo: UserInfo?,
    isLoading: Boolean,
    error: String?,
    services: List<RepairServiceDto>,
    bookings: List<BookingSummaryDto>,
    onQuickBookClick: () -> Unit,
    onViewServicesClick: () -> Unit,
    onServiceClick: (RepairServiceDto) -> Unit,
    onViewInProgressClick: () -> Unit,
    onViewCompletedClick: () -> Unit
) {
    val totalServices = services.size
    val inProgressBookings = bookings.count { it.status == "IN_PROGRESS" }
    val completedBookings = bookings.count { it.status == "COMPLETED" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            DashboardHero(
                userInfo = userInfo,
                isLoading = isLoading,
                onQuickBookClick = onQuickBookClick
            )
        }
        item { DashboardQuickStats(totalServices, inProgressBookings, completedBookings, onViewServicesClick, onViewInProgressClick, onViewCompletedClick) }
        item { DashboardActionGrid(onQuickBookClick, onViewServicesClick) }
        item { DashboardSectionHeader("Trạng thái gần đây") }
        item { DashboardStatusTimeline(bookings.take(3)) }
        item { DashboardSectionHeader("Gợi ý dịch vụ") }
        item { DashboardRecommendedServices(services.take(3), onServiceClick) }
        
        if (!error.isNullOrBlank()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun DashboardHero(userInfo: UserInfo?, isLoading: Boolean, onQuickBookClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInfo?.name?.take(1)?.uppercase() ?: "U",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quản lý dịch vụ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = userInfo?.name ?: "Khách hàng",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1
                    )
                }
            }
            Text(
                text = if (isLoading) "Đang đồng bộ..." else "Đặt lịch nhanh và theo dõi trạng thái dễ dàng.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onQuickBookClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đặt lịch ngay")
                }
            }
        }
    }
}

@Composable
fun DashboardQuickStats(
    totalServices: Int,
    inProgressBookings: Int,
    completedBookings: Int,
    onServicesClick: () -> Unit,
    onInProgressClick: () -> Unit,
    onCompletedClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        @Suppress("DEPRECATION")
        DashboardStatCard(Modifier.weight(1f).fillMaxHeight(), totalServices.toString(), "Dịch vụ", Icons.Default.List, onServicesClick)
        DashboardStatCard(Modifier.weight(1f).fillMaxHeight(), String.format(Locale.US, "%02d", inProgressBookings), "Đang xử lý", Icons.Default.PendingActions, onInProgressClick)
        DashboardStatCard(Modifier.weight(1f).fillMaxHeight(), completedBookings.toString(), "Hoàn thành", Icons.Default.CheckCircle, onCompletedClick)
    }
}

@Composable
fun DashboardStatCard(modifier: Modifier = Modifier, value: String, label: String, icon: ImageVector, onClick: (() -> Unit)? = null) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                label, 
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Composable
fun DashboardActionGrid(onQuickBookClick: () -> Unit, onViewServicesClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DashboardSectionHeader("Lối tắt")
        DashboardActionCard("Tạo booking mới", "Đặt lịch sửa chữa tận nơi", Icons.Default.Build, onQuickBookClick)
        DashboardActionCard("Xem dịch vụ", "Khám phá các dịch vụ hiện có", Icons.Default.Search, onViewServicesClick)
    }
}

@Composable
fun DashboardActionCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DashboardStatusTimeline(recentBookings: List<BookingSummaryDto>) {
    if (recentBookings.isEmpty()) {
        Text("Chưa có đơn hàng nào.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            recentBookings.forEach { booking ->
                val timeStr = try {
                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(booking.createdAt ?: "")
                    if (date != null) dateFormat.format(date) else ""
                } catch (e: Exception) { "" }
                val done = booking.status == "COMPLETED"
                
                val titleStr = "Đơn ${booking.bookingCode} - ${
                    when(booking.status) {
                        "PENDING" -> "Chờ xác nhận"
                        "CONFIRMED" -> "Đã xác nhận"
                        "IN_PROGRESS" -> "Đang xử lý"
                        "COMPLETED" -> "Hoàn thành"
                        "CANCELLED" -> "Đã hủy"
                        else -> booking.status
                    }
                }"
                
                DashboardTimelineItem(titleStr, "Tạo ngày: $timeStr", done)
            }
        }
    }
}

@Composable
fun DashboardTimelineItem(title: String, time: String, done: Boolean) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            if (done) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun DashboardRecommendedServices(services: List<RepairServiceDto>, onServiceClick: (RepairServiceDto) -> Unit) {
    if (services.isEmpty()) {
        Text("Chưa có dịch vụ nào.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            services.forEach { service ->
                val priceFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                DashboardServiceCard(
                    title = service.name,
                    description = service.shortDescription ?: "Không có mô tả",
                    price = priceFormatter.format(service.basePrice),
                    onClick = { onServiceClick(service) }
                )
            }
        }
    }
}

@Composable
fun DashboardServiceCard(title: String, description: String, price: String, onClick: (() -> Unit)? = null) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick ?: {}
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(price, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DashboardSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}
