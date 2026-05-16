package vn.vibe.booking.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.domain.model.UserInfo

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    token: String?,
    onLogout: () -> Unit,
    contentPadding: androidx.compose.foundation.layout.PaddingValues
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(token) { viewModel.loadUserInfo(token) }

    val userInfo = (userState as? UiState.Success<UserInfo>)?.data
    val isLoading = userState is UiState.Loading
    val error = (userState as? UiState.Error)?.message

    val bg = Brush.linearGradient(colors = listOf(Color(0xFF050816), Color(0xFF0B1026), Color(0xFF170E2E)))
    val accent = Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4), Color(0xFFEC4899)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(contentPadding)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TopAppBar(
                title = userInfo?.name ?: "Admin VIPPRO",
                subtitle = userInfo?.roleName ?: "Đang đồng bộ thông tin...",
                onMenuClick = { menuExpanded = true }
            )

            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                DropdownMenuItem(text = { Text("Profile") }, onClick = { menuExpanded = false })
                DropdownMenuItem(text = { Text("Settings") }, onClick = { menuExpanded = false })
                DropdownMenuItem(
                    text = { Text("Đăng xuất") },
                    leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    onClick = {
                        menuExpanded = false
                        onLogout()
                    }
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (isLoading) {
                    HeaderSkeleton()
                } else {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(accent),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = userInfo?.avatar,
                                contentDescription = userInfo?.name,
                                modifier = Modifier
                                    .size(66.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userInfo?.name ?: "Người dùng", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(userInfo?.phone ?: "", color = Color(0xFFCBD5E1), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            RoleBadge(text = userInfo?.roleName ?: "-", glowing = true)
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Thông tin tài khoản", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("ID", userInfo?.id?.toString() ?: "-")
                    InfoRow("Email", userInfo?.email ?: "Chưa cập nhật")
                    InfoRow("Role", userInfo?.roleName ?: "-")
                    InfoRow("Trạng thái", if ((userInfo?.phone ?: "").isNotBlank()) "Đã xác minh số điện thoại" else "Chưa xác minh")
                }
            }

            if (error != null) {
                Text(error, color = Color(0xFFFCA5A5))
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất")
            }
        }
    }
}

@Composable
private fun TopAppBar(title: String, subtitle: String, onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF93C5FD), style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
        }
    }
}

@Composable
private fun HeaderSkeleton() { Text("Đang tải...", color = Color(0xFFCBD5E1), modifier = Modifier.padding(18.dp)) }

@Composable
private fun RoleBadge(text: String, glowing: Boolean) {
    AssistChip(
        onClick = {},
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(containerColor = if (glowing) Color(0xFF1E293B) else Color(0xFF111827), labelColor = Color.White)
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color(0xFF94A3B8), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Medium)
        Divider(color = Color(0xFF1F2937), modifier = Modifier.padding(top = 8.dp))
    }
}
