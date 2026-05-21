package vn.vibe.booking.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import vn.vibe.booking.domain.model.UserInfo

@Composable
fun HomeDashboard(userInfo: UserInfo?, isLoading: Boolean, error: String?, accent: Brush) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeroCard(userInfo = userInfo, accent = accent, isLoading = isLoading) }
        item { QuickStats() }
        item { ActionGrid() }
        item { SectionHeader("Trạng thái gần đây") }
        item { StatusTimeline() }
        item { SectionHeader("Gợi ý dịch vụ") }
        item { RecommendedServices() }
        if (!error.isNullOrBlank()) item { ErrorBanner(error) }
    }
}

@Composable
fun ServiceCatalogScreen(search: String, onSearchChange: (String) -> Unit) {
    val services = listOf(
        Triple("Thay màn hình", "Màn hình nứt, sọc, không hiển thị", "2.500.000đ"),
        Triple("Thay pin", "Pin chai, tụt nhanh, không sạc", "1.200.000đ"),
        Triple("Vệ sinh laptop", "Bảo dưỡng, thay keo tản nhiệt", "350.000đ"),
        Triple("Sửa nguồn", "Không lên nguồn, chập chờn", "Liên hệ báo giá")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Tìm kiếm dịch vụ, danh mục...") },
                singleLine = true
            )
        }
        item { SectionHeader("Danh mục nổi bật") }
        items(services.filter { it.first.contains(search, ignoreCase = true) || it.second.contains(search, ignoreCase = true) }) { service ->
            ServiceCard(title = service.first, description = service.second, price = service.third)
        }
    }
}

@Composable
fun BookingTimelineScreen() {
    val bookings = listOf(
        Triple("BK202605200001", "PENDING_CONFIRMATION", "09:00 - 11:00"),
        Triple("BK202605190014", "IN_PROGRESS", "13:00 - 15:00"),
        Triple("BK202605180027", "COMPLETED", "15:00 - 17:00")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { SectionHeader("Lịch hẹn của bạn") }
        items(bookings) { booking ->
            BookingCard(code = booking.first, status = booking.second, time = booking.third)
        }
    }
}

@Composable
fun ProfileScreen(userInfo: UserInfo?, accent: Brush, onLogout: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeroCard(userInfo = userInfo, accent = accent, isLoading = false) }
        item { ProfileDetails(userInfo = userInfo) }
        item { SettingsActions(onLogout = onLogout) }
    }
}

@Composable
fun HeroCard(userInfo: UserInfo?, accent: Brush, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(avatarUrl = userInfo?.avatar, accent = accent, size = 52.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Quản lý sửa chữa chuyên nghiệp", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Text(userInfo?.name ?: "Tài khoản của bạn", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                }
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF8B5CF6))
            }
            Text(text = if (isLoading) "Đang đồng bộ dữ liệu người dùng..." else "Đặt lịch, theo dõi trạng thái và quản lý dịch vụ trong một giao diện gọn hơn.", color = Color(0xFFCBD5E1), fontSize = 12.sp, maxLines = 2)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Đặt lịch nhanh") }, leadingIcon = { Icon(Icons.Default.Schedule, null) })
                AssistChip(onClick = {}, label = { Text("Hỗ trợ 24/7") }, leadingIcon = { Icon(Icons.Default.SupportAgent, null) })
            }
        }
    }
}

@Composable fun QuickStats() { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { Box(modifier = Modifier.weight(1f)) { StatCard("12", "Dịch vụ", Icons.Default.BookOnline) }; Box(modifier = Modifier.weight(1f)) { StatCard("08", "Đặt lịch", Icons.Default.BookOnline) }; Box(modifier = Modifier.weight(1f)) { StatCard("4.9", "Đánh giá", Icons.Default.RateReview) } } }
@Composable fun ActionGrid() { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { SectionHeader("Thao tác nhanh"); ActionCard("Tạo booking", "Đặt lịch sửa chữa trong vài bước", Icons.Default.BookOnline); ActionCard("Theo dõi trạng thái", "Xem tiến trình sửa chữa theo thời gian thực", Icons.Default.History); ActionCard("Xem danh mục", "Khám phá các dịch vụ phổ biến", Icons.Default.LocalOffer) } }
@Composable fun StatusTimeline() { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { TimelineItem("Đã gửi yêu cầu", "09:00 - 20/05", true); TimelineItem("Đã xác nhận", "09:20 - 20/05", true); TimelineItem("Đang sửa chữa", "10:15 - 20/05", false) } }
@Composable fun RecommendedServices() { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { ServiceCard("Vệ sinh laptop", "Tối ưu nhiệt độ và hiệu năng", "350.000đ"); ServiceCard("Thay SSD", "Nâng cấp tốc độ máy", "1.100.000đ") } }
@Composable fun ProfileDetails(userInfo: UserInfo?) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text("Thông tin tài khoản", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Text("Xem thêm", color = Color(0xFF8B5CF6), fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }; InfoRow(Icons.Default.Person, "Họ tên", userInfo?.name ?: "Chưa có dữ liệu"); InfoRow(Icons.Default.Phone, "Số điện thoại", userInfo?.phone ?: "Chưa cập nhật"); InfoRow(Icons.Default.Email, "Email", userInfo?.email ?: "Chưa cập nhật") } } }
@Composable fun SettingsActions(onLogout: () -> Unit) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text("Cài đặt", color = Color.White, fontWeight = FontWeight.Bold); SettingRow(Icons.Default.Info, "Trung tâm trợ giúp"); SettingRow(Icons.Default.Settings, "Tuỳ chỉnh tài khoản"); SettingRow(Icons.Default.ExitToApp, "Đăng xuất", onClick = onLogout) } } }
@Composable fun ErrorBanner(message: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3B0A1E)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) { Text(message, color = Color(0xFFFCA5A5), modifier = Modifier.padding(16.dp)) } }
@Composable fun SectionHeader(title: String) { Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
@Composable fun StatCard(value: String, label: String, icon: ImageVector) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Icon(icon, null, tint = Color(0xFF8B5CF6)); Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp); Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp) } } }
@Composable fun ActionCard(title: String, subtitle: String, icon: ImageVector) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF111827)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color(0xFF06B6D4)) }; Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(title, color = Color.White, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp) } } } }
@Composable fun BookingCard(code: String, status: String, time: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(code, color = Color.White, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); StatusPill(status) }; Text(time, color = Color(0xFF94A3B8)); Text("Laptop, vệ sinh, kiểm tra phần cứng", color = Color(0xFFCBD5E1), fontSize = 12.sp) } } }
@Composable fun StatusPill(status: String) { val color = when (status) { "COMPLETED" -> Color(0xFF10B981); "IN_PROGRESS" -> Color(0xFFF59E0B); else -> Color(0xFF8B5CF6) }; Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Circle, null, tint = color, modifier = Modifier.size(10.dp)); Spacer(Modifier.width(6.dp)); Text(status, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) } }
@Composable fun TimelineItem(title: String, time: String, done: Boolean) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(if (done) Icons.Default.CheckCircle else Icons.Default.Circle, null, tint = if (done) Color(0xFF10B981) else Color(0xFF94A3B8)); Spacer(Modifier.width(12.dp)); Column { Text(title, color = Color.White); Text(time, color = Color(0xFF94A3B8), fontSize = 12.sp) } } }
@Composable fun ServiceCard(title: String, description: String, price: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(title, color = Color.White, fontWeight = FontWeight.Bold); Text(description, color = Color(0xFFCBD5E1), fontSize = 12.sp); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocalOffer, null, tint = Color(0xFF06B6D4)); Spacer(Modifier.width(6.dp)); Text(price, color = Color(0xFF06B6D4), fontWeight = FontWeight.SemiBold) } } } }
@Composable fun SettingRow(icon: ImageVector, title: String, onClick: (() -> Unit)? = null) { Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Color(0xFF8B5CF6)); Spacer(Modifier.width(12.dp)); Text(title, color = Color.White); if (onClick != null) { IconButton(onClick = onClick) { Icon(Icons.Default.ExitToApp, null, tint = Color(0xFFF87171)) } } }; HorizontalDivider(color = Color(0xFF1F2937)) }
@Composable fun Avatar(avatarUrl: String?, accent: Brush, size: Dp = 68.dp) { Box(modifier = Modifier.size(size).clip(CircleShape).background(accent), contentAlignment = Alignment.Center) { AsyncImage(model = avatarUrl, contentDescription = null, modifier = Modifier.size(size - 6.dp).clip(CircleShape).background(Color(0xFF0F172A)), contentScale = ContentScale.Crop) } }
@Composable fun InfoRow(icon: ImageVector, label: String, value: String) { Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Icon(icon, contentDescription = null, tint = Color(0xFF8B5CF6)); Spacer(modifier = Modifier.width(12.dp)); Column(modifier = Modifier.weight(1f)) { Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp); Text(value, color = Color.White, fontWeight = FontWeight.Medium) } } }
