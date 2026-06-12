package vn.vibe.booking.presentation.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.data.remote.AdminUserDto
import vn.vibe.booking.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(
    viewModel: HomeViewModel,
    token: String?,
    onBack: () -> Unit
) {
    val usersState by viewModel.usersState.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editUser by remember { mutableStateOf<AdminUserDto?>(null) }
    var deleteUser by remember { mutableStateOf<AdminUserDto?>(null) }
    var filterRole by remember { mutableStateOf<String?>(null) }

    val roleFilters = listOf(null to "Tất cả", "ADMIN" to "Admin", "TECHNICIAN" to "Kỹ thuật viên", "CUSTOMER" to "Khách hàng")

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý User") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadUsers(token) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm User")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Role filter
            ScrollableTabRow(
                selectedTabIndex = roleFilters.indexOfFirst { it.first == filterRole }.coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                divider = {}
            ) {
                roleFilters.forEachIndexed { _, (role, label) ->
                    Tab(
                        selected = filterRole == role,
                        onClick = {
                            filterRole = role
                            viewModel.loadUsers(token, role = role)
                        },
                        text = { Text(label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            HorizontalDivider()

            if (usersState.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (usersState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có user nào", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(usersState.items) { user ->
                        UserCard(
                            user = user,
                            onEdit = { editUser = user },
                            onDelete = { deleteUser = user },
                            onToggleActive = {
                                token?.let {
                                    viewModel.setUserActive(it, user.id, !user.active) {
                                        viewModel.loadUsers(token, role = filterRole)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Create User Dialog
        if (showCreateDialog) {
            UserFormDialog(
                title = "Tạo User mới",
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, phone, email, role ->
                    token?.let {
                        viewModel.createUser(it, name, phone, email, role, true) {
                            showCreateDialog = false
                            viewModel.loadUsers(token, role = filterRole)
                        }
                    }
                }
            )
        }

        // Edit User Dialog
        editUser?.let { eUser ->
            UserFormDialog(
                title = "Sửa User",
                initialName = eUser.name,
                initialPhone = eUser.phone ?: "",
                initialEmail = eUser.email ?: "",
                initialRole = eUser.role,
                onDismiss = { editUser = null },
                onConfirm = { name, phone, email, role ->
                    token?.let { t ->
                        viewModel.updateUser(t, eUser.id, name, phone, email, role, eUser.active) {
                            editUser = null
                            viewModel.loadUsers(token, role = filterRole)
                        }
                    }
                }
            )
        }

        // Delete Confirmation
        deleteUser?.let { dUser ->
            AlertDialog(
                onDismissRequest = { deleteUser = null },
                title = { Text("Xóa User") },
                text = { Text("Bạn có chắc muốn xóa ${dUser.name}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            token?.let { t ->
                                viewModel.deleteUser(t, dUser.id) {
                                    deleteUser = null
                                    viewModel.loadUsers(token, role = filterRole)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("Xóa") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteUser = null }) { Text("Hủy") }
                }
            )
        }
    }
}

@Composable
fun UserCard(
    user: AdminUserDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (user.active) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = if (user.active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column {
                        Text(user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(
                            user.role,
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                user.role.equals("ADMIN", ignoreCase = true) -> MaterialTheme.colorScheme.error
                                user.role.equals("TECHNICIAN", ignoreCase = true) -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
                Switch(
                    checked = user.active,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }

            user.phone?.let {
                Text("📞 $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            user.email?.let {
                Text("✉️ $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sửa")
                }
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Xóa")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormDialog(
    title: String,
    initialName: String = "",
    initialPhone: String = "",
    initialEmail: String = "",
    initialRole: String = "CUSTOMER",
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String?, email: String?, role: String) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var phone by remember { mutableStateOf(initialPhone) }
    var email by remember { mutableStateOf(initialEmail) }
    var role by remember { mutableStateOf(initialRole) }
    var roleExpanded by remember { mutableStateOf(false) }
    val roles = listOf("ADMIN", "TECHNICIAN", "CUSTOMER")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Họ tên *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = it }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vai trò") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r) },
                                onClick = { role = r; roleExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, phone.ifBlank { null }, email.ifBlank { null }, role) },
                enabled = name.isNotBlank()
            ) { Text("Lưu") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
