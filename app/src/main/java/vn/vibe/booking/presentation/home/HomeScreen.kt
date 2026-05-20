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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.domain.model.UserInfo

enum class HomeTab(val label: String, val icon: ImageVector) {
    Home("Trang chủ", Icons.Default.Home),
    Services("Dịch vụ", Icons.Default.Construction),
    Bookings("Lịch hẹn", Icons.Default.BookOnline),
    Profile("Tài khoản", Icons.Default.Person)
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    token: String?,
    onLogout: () -> Unit,
    contentPadding: androidx.compose.foundation.layout.PaddingValues
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    var search by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(token) {
        viewModel.loadUserInfo(token)
    }

    val userInfo = (userState as? UiState.Success<UserInfo>)?.data
    val isLoading = userState is UiState.Loading
    val error = (userState as? UiState.Error)?.message

    val screenBg = Brush.linearGradient(
        colors = listOf(Color(0xFF050816), Color(0xFF0B1220), Color(0xFF111827))
    )
    val accent = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .padding(contentPadding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(userInfo = userInfo, onMenuClick = { menuExpanded = true })

            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(
                    text = { Text("Đăng xuất") },
                    leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onLogout()
                    }
                )
            }

            NavigationTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Box(modifier = Modifier.fillMaxSize()) {
                when (HomeTab.entries[selectedTab]) {
                    HomeTab.Home -> HomeDashboard(userInfo = userInfo, isLoading = isLoading, error = error, accent = accent)
                    HomeTab.Services -> ServiceCatalogScreen(search = search, onSearchChange = { search = it })
                    HomeTab.Bookings -> BookingTimelineScreen()
                    HomeTab.Profile -> ProfileScreen(userInfo = userInfo, accent = accent, onLogout = onLogout)
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(userInfo: UserInfo?, onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(avatarUrl = userInfo?.avatar, accent = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4))))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Repair Booking", color = Color(0xFF94A3B8), fontSize = 12.sp)
            Text(
                text = userInfo?.name ?: "Chào mừng bạn",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun NavigationTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        HomeTab.entries.forEachIndexed { index, tab ->
            FilterChip(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                label = { Text(tab.label) },
                leadingIcon = { Icon(tab.icon, contentDescription = null) }
            )
        }
    }
}

@Composable
private fun HomeDashboard(userInfo: UserInfo?, isLoading: Boolean, error: String?, accent: Brush) {
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
        if (!error.isNullOrBlank()) {
            item { ErrorBanner(error) }
        }
    }
}

@Composable
private fun ServiceCatalogScreen(search: String, onSearchChange: (String) -> Unit) {
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
private fun BookingTimelineScreen() {
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
private fun ProfileScreen(userInfo: UserInfo?, accent: Brush, onLogout: () -> Unit) {
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
private fun HeroCard(userInfo: UserInfo?, accent: Brush, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(avatarUrl = userInfo?.avatar, accent = accent, size = 62.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Quản lý sửa chữa chuyên nghiệp", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Text(userInfo?.name ?: "Tài khoản của bạn", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF8B5CF6))
            }
            Text(
                text = if (isLoading) "Đang đồng bộ dữ liệu người dùng..." else "Đặt lịch, theo dõi trạng thái, quản lý dịch vụ và đánh giá trong một giao diện tối giản, rõ ràng.",
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AssistChip(onClick = {}, label = { Text("Đặt lịch nhanh") }, leadingIcon = { Icon(Icons.Default.Schedule, null) })
                AssistChip(onClick = {}, label = { Text("Hỗ trợ 24/7") }, leadingIcon = { Icon(Icons.Default.SupportAgent, null) })
            }
        }
    }
}

@Composable private fun QuickStats() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.weight(1f)) { StatCard("12", "Dịch vụ", Icons.Default.Category) }
        Box(modifier = Modifier.weight(1f)) { StatCard("08", "Đặt lịch", Icons.Default.BookOnline) }
        Box(modifier = Modifier.weight(1f)) { StatCard("4.9", "Đánh giá", Icons.Default.RateReview) }
    }
}
@Composable private fun ActionGrid() { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { SectionHeader("Thao tác nhanh"); ActionCard("Tạo booking", "Đặt lịch sửa chữa trong vài bước", Icons.Default.BookOnline); ActionCard("Theo dõi trạng thái", "Xem tiến trình sửa chữa theo thời gian thực", Icons.Default.History); ActionCard("Xem danh mục", "Khám phá các dịch vụ phổ biến", Icons.Default.LocalOffer) } }
@Composable private fun StatusTimeline() { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { TimelineItem("Đã gửi yêu cầu", "09:00 - 20/05", true); TimelineItem("Đã xác nhận", "09:20 - 20/05", true); TimelineItem("Đang sửa chữa", "10:15 - 20/05", false) } }
@Composable private fun RecommendedServices() { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { ServiceCard("Vệ sinh laptop", "Tối ưu nhiệt độ và hiệu năng", "350.000đ"); ServiceCard("Thay SSD", "Nâng cấp tốc độ máy", "1.100.000đ") } }
@Composable private fun ProfileDetails(userInfo: UserInfo?) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Text("Thông tin tài khoản", color = Color.White, fontWeight = FontWeight.Bold); InfoRow(Icons.Default.Person, "Họ tên", userInfo?.name ?: "Chưa có dữ liệu"); InfoRow(Icons.Default.Phone, "Số điện thoại", userInfo?.phone ?: "Chưa cập nhật"); InfoRow(Icons.Default.Email, "Email", userInfo?.email ?: "Chưa cập nhật"); InfoRow(Icons.Default.Verified, "Vai trò", userInfo?.roleName ?: "USER") } } }
@Composable private fun SettingsActions(onLogout: () -> Unit) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Text("Cài đặt", color = Color.White, fontWeight = FontWeight.Bold); SettingRow(Icons.Default.Info, "Trung tâm trợ giúp"); SettingRow(Icons.Default.Settings, "Tuỳ chỉnh tài khoản"); SettingRow(Icons.Default.ExitToApp, "Đăng xuất", onClick = onLogout) } } }

@Composable private fun ErrorBanner(message: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3B0A1E)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) { Text(message, color = Color(0xFFFCA5A5), modifier = Modifier.padding(16.dp)) } }
@Composable private fun SectionHeader(title: String) { Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }

@Composable private fun StatCard(value: String, label: String, icon: ImageVector) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { Icon(icon, null, tint = Color(0xFF8B5CF6)); Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp); Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp) } } }
@Composable private fun ActionCard(title: String, subtitle: String, icon: ImageVector) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF111827)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Color(0xFF06B6D4)) }; Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(title, color = Color.White, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Color(0xFF94A3B8), fontSize = 12.sp) } } } }
@Composable private fun BookingCard(code: String, status: String, time: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(code, color = Color.White, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); StatusPill(status) }; Text(time, color = Color(0xFF94A3B8)); Text("Laptop, vệ sinh, kiểm tra phần cứng", color = Color(0xFFCBD5E1), fontSize = 12.sp) } } }
@Composable private fun StatusPill(status: String) { val color = when (status) { "COMPLETED" -> Color(0xFF10B981); "IN_PROGRESS" -> Color(0xFFF59E0B); else -> Color(0xFF8B5CF6) }; Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Circle, null, tint = color, modifier = Modifier.size(10.dp)); Spacer(Modifier.width(6.dp)); Text(status, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) } }
@Composable private fun TimelineItem(title: String, time: String, done: Boolean) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(if (done) Icons.Default.CheckCircle else Icons.Default.Circle, null, tint = if (done) Color(0xFF10B981) else Color(0xFF94A3B8)); Spacer(Modifier.width(12.dp)); Column { Text(title, color = Color.White); Text(time, color = Color(0xFF94A3B8), fontSize = 12.sp) } } }
@Composable private fun ServiceCard(title: String, description: String, price: String) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(title, color = Color.White, fontWeight = FontWeight.Bold); Text(description, color = Color(0xFFCBD5E1), fontSize = 12.sp); Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocalOffer, null, tint = Color(0xFF06B6D4)); Spacer(Modifier.width(6.dp)); Text(price, color = Color(0xFF06B6D4), fontWeight = FontWeight.SemiBold) } } } }
@Composable private fun SettingRow(icon: ImageVector, title: String, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF8B5CF6))
        Spacer(Modifier.width(12.dp))
        Text(title, color = Color.White)
        if (onClick != null) {
            IconButton(onClick = onClick) {
                Icon(Icons.Default.ExitToApp, null, tint = Color(0xFFF87171))
            }
        }
    }
    HorizontalDivider(color = Color(0xFF1F2937))
}

@Composable
private fun Avatar(avatarUrl: String?, accent: Brush, size: Dp = 68.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(accent),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(size - 6.dp)
                .clip(CircleShape)
                .background(Color(0xFF0F172A)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF8B5CF6))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}
