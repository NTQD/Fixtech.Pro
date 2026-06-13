package vn.vibe.booking.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import vn.vibe.booking.domain.model.UserInfo

@Composable
fun ProfileScreen(
    userInfo: UserInfo?,
    onLogout: () -> Unit,
    onViewBookingHistory: () -> Unit,
    onUpdateProfile: (name: String, email: String, password: String?) -> Unit = { _, _, _ -> }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            ProfileHeroCard(userInfo = userInfo, onUpdateProfile = onUpdateProfile)
        }
        item {
            ProfileDetailsSection(userInfo = userInfo)
        }
        item {
            ProfileSettingsSection(onLogout = onLogout, onViewBookingHistory = onViewBookingHistory)
        }
    }
}

@Composable
fun ProfileHeroCard(
    userInfo: UserInfo?,
    onUpdateProfile: (name: String, email: String, password: String?) -> Unit = { _, _, _ -> }
) {
    var showEditDialog by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInfo?.name?.take(1)?.uppercase() ?: "U",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = userInfo?.name ?: "Người dùng ẩn danh",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userInfo?.role ?: "Khách hàng",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { showEditDialog = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cập nhật thông tin")
            }
        }
    }

    if (showEditDialog) {
        var name by remember { mutableStateOf(userInfo?.name ?: "") }
        var phone by remember { mutableStateOf(userInfo?.phone ?: "") }
        var email by remember { mutableStateOf(userInfo?.email ?: "") }
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Cập nhật hồ sơ") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = false
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu mới (Bỏ trống nếu không đổi)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    Text(
                        "Lưu ý: Chức năng cập nhật sẽ yêu cầu kết nối API.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateProfile(name, email, password.takeIf { it.isNotBlank() })
                    showEditDialog = false
                }) { Text("Lưu") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Hủy") }
            }
        )
    }
}

@Composable
fun ProfileDetailsSection(userInfo: UserInfo?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Thông tin liên hệ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                ProfileInfoRow(icon = Icons.Default.Person, label = "Họ và tên", value = userInfo?.name ?: "Chưa cập nhật")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                ProfileInfoRow(icon = Icons.Default.Phone, label = "Số điện thoại", value = userInfo?.phone ?: "Chưa cập nhật")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                ProfileInfoRow(icon = Icons.Default.Email, label = "Email", value = userInfo?.email ?: "Chưa cập nhật")
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileSettingsSection(onLogout: () -> Unit, onViewBookingHistory: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Cài đặt & Hỗ trợ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
        ) {
            Column(Modifier.padding(vertical = 8.dp)) {
                ProfileSettingItem(icon = Icons.Default.History, title = "Lịch sử đặt lịch", onClick = onViewBookingHistory)
                ProfileSettingItem(icon = Icons.Default.QuestionMark, title = "Trung tâm trợ giúp")
                ProfileSettingItem(icon = Icons.Default.Info, title = "Điều khoản dịch vụ")
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)
                ProfileSettingItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Đăng xuất",
                    iconTint = MaterialTheme.colorScheme.error,
                    textColor = MaterialTheme.colorScheme.error,
                    onClick = onLogout
                )
            }
        }
    }
}

@Composable
fun ProfileSettingItem(
    icon: ImageVector,
    title: String,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, color = textColor, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
