package vn.vibe.booking.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.data.remote.AdminUserDto
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.ServiceCategoryDto
import vn.vibe.booking.domain.model.UiState

private enum class ConsoleSection(val title: String) { Users("Users"), Services("Services"), Categories("Categories"), Bookings("Bookings") }

@Composable
fun AdminConsoleScreen(
    viewModel: HomeViewModel,
    token: String?,
    contentPadding: PaddingValues
) {
    LaunchedEffect(token) {
        if (token != null) {
            viewModel.loadUsers(token)
            viewModel.loadServices(token)
            viewModel.loadCategories(token)
            viewModel.loadBookings(token)
        }
    }

    val users by viewModel.usersState.collectAsStateWithLifecycle()
    val services by viewModel.servicesState.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesState.collectAsStateWithLifecycle()
    val bookings by viewModel.bookingsState.collectAsStateWithLifecycle()

    var selectedSection by remember { mutableStateOf(ConsoleSection.Users) }
    var search by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMode by remember { mutableStateOf("create") }
    var dialogTitle by remember { mutableStateOf("") }

    val usersList = (users as? UiState.Success)?.data.orEmpty()
    val servicesList = (services as? UiState.Success)?.data.orEmpty()
    val categoriesList = (categories as? UiState.Success)?.data.orEmpty()
    val bookingsList = (bookings as? UiState.Success)?.data.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ConsoleHeader()
        ConsoleSummaryGrid(users, services, categories, bookings)
        ConsoleShortcuts(selectedSection = selectedSection, onSelect = { selectedSection = it })

        when (selectedSection) {
            ConsoleSection.Users -> AdminSectionToolbar(
                search = search,
                onSearchChange = { search = it },
                extraFilterLabel = selectedRole ?: "All roles",
                onAdd = {
                    dialogMode = "create"
                    dialogTitle = "Create user"
                    showDialog = true
                }
            )
            ConsoleSection.Services -> AdminSectionToolbar(
                search = search,
                onSearchChange = { search = it },
                extraFilterLabel = "Category",
                onAdd = {
                    dialogMode = "create"
                    dialogTitle = "Create service"
                    showDialog = true
                }
            )
            ConsoleSection.Categories -> AdminSectionToolbar(
                search = search,
                onSearchChange = { search = it },
                extraFilterLabel = "Active",
                onAdd = {
                    dialogMode = "create"
                    dialogTitle = "Create category"
                    showDialog = true
                }
            )
            ConsoleSection.Bookings -> AdminSectionToolbar(
                search = search,
                onSearchChange = { search = it },
                extraFilterLabel = selectedStatus ?: "All status",
                onAdd = {}
            )
        }

        when (selectedSection) {
            ConsoleSection.Users -> UsersTable(users, search, selectedRole, onEdit = { dialogMode = "edit"; dialogTitle = "Edit user"; showDialog = true })
            ConsoleSection.Services -> ServicesTable(services, search, onEdit = { dialogMode = "edit"; dialogTitle = "Edit service"; showDialog = true })
            ConsoleSection.Categories -> CategoriesTable(categories, search, onEdit = { dialogMode = "edit"; dialogTitle = "Edit category"; showDialog = true })
            ConsoleSection.Bookings -> BookingsTable(bookings, search, selectedStatus, onEdit = { dialogMode = "edit"; dialogTitle = "Update booking"; showDialog = true })
        }
    }

    if (showDialog) {
        ActionDialog(
            title = dialogTitle,
            onDismiss = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }
}

@Composable
private fun ConsoleHeader() {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Admin Console", color = Color(0xFF94A3B8))
            Text("Quản lý booking, dịch vụ và người dùng", color = Color.White, fontWeight = FontWeight.Bold)
            Text("Tìm kiếm, lọc, xem dạng bảng và thao tác CRUD ngay trong từng màn.", color = Color(0xFFCBD5E1))
        }
    }
}

@Composable
private fun ConsoleSummaryGrid(
    users: UiState<List<AdminUserDto>>,
    services: UiState<List<RepairServiceDto>>,
    categories: UiState<List<ServiceCategoryDto>>,
    bookings: UiState<List<BookingSummaryDto>>
) {
    val userCount = (users as? UiState.Success)?.data?.size ?: 0
    val serviceCount = (services as? UiState.Success)?.data?.size ?: 0
    val categoryCount = (categories as? UiState.Success)?.data?.size ?: 0
    val bookingCount = (bookings as? UiState.Success)?.data?.size ?: 0
    Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        MiniStatCard("Users", "$userCount", modifier = Modifier.weight(1f))
        MiniStatCard("Services", "$serviceCount", modifier = Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
        MiniStatCard("Categories", "$categoryCount", modifier = Modifier.weight(1f))
        MiniStatCard("Bookings", "$bookingCount", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MiniStatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = Color(0xFF94A3B8))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ConsoleShortcuts(selectedSection: ConsoleSection, onSelect: (ConsoleSection) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        FilterChip(selected = selectedSection == ConsoleSection.Users, onClick = { onSelect(ConsoleSection.Users) }, label = { Text("Users") }, leadingIcon = { Icon(Icons.Default.People, null) })
        FilterChip(selected = selectedSection == ConsoleSection.Services, onClick = { onSelect(ConsoleSection.Services) }, label = { Text("Services") }, leadingIcon = { Icon(Icons.Default.Category, null) })
        FilterChip(selected = selectedSection == ConsoleSection.Bookings, onClick = { onSelect(ConsoleSection.Bookings) }, label = { Text("Bookings") }, leadingIcon = { Icon(Icons.Default.Work, null) })
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        FilterChip(selected = selectedSection == ConsoleSection.Categories, onClick = { onSelect(ConsoleSection.Categories) }, label = { Text("Categories") }, leadingIcon = { Icon(Icons.Default.Build, null) })
    }
}

@Composable
private fun AdminSectionToolbar(search: String, onSearchChange: (String) -> Unit, extraFilterLabel: String, onAdd: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search by keyword") },
            singleLine = true
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            AssistChip(onClick = {}, label = { Text(extraFilterLabel) }, leadingIcon = { Icon(Icons.Default.FilterAlt, null) })
            Spacer(Modifier.weight(1f))
            Button(onClick = onAdd, enabled = true) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add")
            }
        }
    }
}

@Composable
private fun UsersTable(
    state: UiState<List<AdminUserDto>>,
    search: String,
    roleFilter: String?,
    onEdit: (AdminUserDto) -> Unit
) {
    SectionTable(
        title = "Users",
        headers = listOf("Name", "Role", "Active", "Actions"),
        state = state,
        emptyText = "Chưa có users",
    ) { user ->
        if (search.isNotBlank() && !user.name.contains(search, true) && !(user.email ?: "").contains(search, true)) return@SectionTable
        if (roleFilter != null && !user.role.equals(roleFilter, true)) return@SectionTable
        TableRow(
            cells = listOf(user.name, user.role, if (user.active) "Active" else "Locked"),
            onView = {},
            onEdit = { onEdit(user) },
            onDelete = {}
        )
    }
}

@Composable
private fun ServicesTable(
    state: UiState<List<RepairServiceDto>>,
    search: String,
    onEdit: (RepairServiceDto) -> Unit
) {
    SectionTable("Services", listOf("Name", "Category", "Price", "Actions"), state, "Chưa có services") { service ->
        if (search.isNotBlank() && !service.name.contains(search, true) && !(service.shortDescription ?: "").contains(search, true)) return@SectionTable
        TableRow(
            cells = listOf(service.name, service.categoryId.toString(), service.basePrice.toString()),
            onView = {},
            onEdit = { onEdit(service) },
            onDelete = {}
        )
    }
}

@Composable
private fun CategoriesTable(
    state: UiState<List<ServiceCategoryDto>>,
    search: String,
    onEdit: (ServiceCategoryDto) -> Unit
) {
    SectionTable("Categories", listOf("Name", "Active", "Actions"), state, "Chưa có categories") { category ->
        if (search.isNotBlank() && !category.name.contains(search, true) && !(category.description ?: "").contains(search, true)) return@SectionTable
        TableRow(
            cells = listOf(category.name, if (category.active) "Active" else "Inactive"),
            onView = {},
            onEdit = { onEdit(category) },
            onDelete = {}
        )
    }
}

@Composable
private fun BookingsTable(
    state: UiState<List<BookingSummaryDto>>,
    search: String,
    statusFilter: String?,
    onEdit: (BookingSummaryDto) -> Unit
) {
    SectionTable("Bookings", listOf("Code", "Status", "Price", "Actions"), state, "Chưa có bookings") { booking ->
        if (search.isNotBlank() && !booking.bookingCode.contains(search, true)) return@SectionTable
        if (statusFilter != null && !booking.status.equals(statusFilter, true)) return@SectionTable
        TableRow(
            cells = listOf(booking.bookingCode, booking.status, booking.totalEstimatedPrice.toString()),
            onView = {},
            onEdit = { onEdit(booking) },
            onDelete = {}
        )
    }
}

@Composable
private fun <T> SectionTable(
    title: String,
    headers: List<String>,
    state: UiState<List<T>>,
    emptyText: String,
    rowContent: @Composable (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
        when (state) {
            is UiState.Success -> {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth()) {
                            headers.forEach { Text(it, color = Color(0xFF94A3B8), modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold) }
                        }
                        state.data.forEach { rowContent(it) }
                    }
                }
            }
            is UiState.Loading -> Text("Đang tải...", color = Color(0xFFCBD5E1))
            is UiState.Empty -> Text(state.message.ifBlank { emptyText }, color = Color(0xFFCBD5E1))
            is UiState.Error -> Text(state.message, color = Color(0xFFFCA5A5))
            else -> Text(emptyText, color = Color(0xFFCBD5E1))
        }
    }
}

@Composable
private fun TableRow(
    cells: List<String>,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        cells.forEach { Text(it, color = Color.White, modifier = Modifier.weight(1f)) }
        IconButton(onClick = onView) { Icon(Icons.Default.Visibility, null, tint = Color(0xFF38BDF8)) }
        IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Color(0xFF8B5CF6)) }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color(0xFFF87171)) }
    }
}

@Composable
private fun ActionDialog(title: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Popup form sẽ được nối API create/edit/delete ở bước kế tiếp.")
                OutlinedTextField(value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Input...") })
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
