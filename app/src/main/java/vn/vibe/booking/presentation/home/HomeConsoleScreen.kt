package vn.vibe.booking.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import vn.vibe.booking.data.remote.AdminUserDto
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.ServiceCategoryDto

private enum class ConsoleSection { Users, Services, Categories, Bookings }

private val BookingStatusOptions = listOf(
    "PENDING",
    "CONFIRMED",
    "ASSIGNED",
    "IN_PROGRESS",
    "COMPLETED",
    "CANCELLED"
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdminConsoleScreen(viewModel: HomeViewModel, token: String?, contentPadding: PaddingValues) {
    val users by viewModel.usersState.collectAsStateWithLifecycle()
    val services by viewModel.servicesState.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesState.collectAsStateWithLifecycle()
    val bookings by viewModel.bookingsState.collectAsStateWithLifecycle()

    var selectedSection by remember { mutableStateOf(ConsoleSection.Users) }
    var search by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf<String?>(null) }
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var pageSize by remember { mutableIntStateOf(20) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogMode by remember { mutableStateOf("create") }
    var dialogTitle by remember { mutableStateOf("") }
    var selectedUser by remember { mutableStateOf<AdminUserDto?>(null) }
    var selectedService by remember { mutableStateOf<RepairServiceDto?>(null) }
    var selectedCategory by remember { mutableStateOf<ServiceCategoryDto?>(null) }
    var selectedBooking by remember { mutableStateOf<BookingSummaryDto?>(null) }
    var deleteTargetUser by remember { mutableStateOf<AdminUserDto?>(null) }
    var userActionError by remember { mutableStateOf<String?>(null) }

    fun reload(page: Int = 1) {
        when (selectedSection) {
            ConsoleSection.Users -> viewModel.loadUsers(token, search.takeIf { it.isNotBlank() }, roleFilter, null, page, pageSize)
            ConsoleSection.Services -> viewModel.loadServices(token, search.takeIf { it.isNotBlank() }, null, page, pageSize)
            ConsoleSection.Categories -> viewModel.loadCategories(token, search.takeIf { it.isNotBlank() }, page, pageSize)
            ConsoleSection.Bookings -> viewModel.loadBookings(token, search.takeIf { it.isNotBlank() }, statusFilter, null, null, page, pageSize)
        }
    }

    LaunchedEffect(token, selectedSection, pageSize) { reload(1) }

    val activeState = when (selectedSection) {
        ConsoleSection.Users -> users
        ConsoleSection.Services -> services
        ConsoleSection.Categories -> categories
        ConsoleSection.Bookings -> bookings
    }
    val currentPage = activeState.page
    val totalPages = maxOf(1, ceil((activeState.total.coerceAtLeast(activeState.items.size)).toDouble() / pageSize).toInt())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ConsoleHeader() }
        item { ConsoleSummaryGrid(users, services, categories, bookings) }
        stickyHeader {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF050816))
                    .zIndex(1f)
                    .padding(bottom = 8.dp)
            ) {
                ConsoleShortcuts(selectedSection = selectedSection, onSelect = {
                    selectedSection = it
                    search = ""
                    roleFilter = null
                    statusFilter = null
                    reload(1)
                })
                AdminSectionToolbar(
                    search = search,
                    onSearchChange = {
                        search = it
                        reload(1)
                    },
                    extraFilterLabel = when (selectedSection) {
                        ConsoleSection.Users -> roleFilter ?: "All roles"
                        ConsoleSection.Bookings -> statusFilter ?: "All status"
                        else -> "All"
                    },
                    onAdd = {
                        dialogMode = "create"
                        dialogTitle = when (selectedSection) {
                            ConsoleSection.Users -> "Create user"
                            ConsoleSection.Services -> "Create service"
                            ConsoleSection.Categories -> "Create category"
                            ConsoleSection.Bookings -> "Confirm / update booking"
                        }
                        showDialog = true
                    }
                )
                PaginationBar(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    pageSize = pageSize,
                    onPageSizeChange = { pageSize = it; reload(1) },
                    onPrev = { if (currentPage > 1) reload(currentPage - 1) },
                    onNext = { if (currentPage < totalPages) reload(currentPage + 1) }
                )
            }
        }
        item {
            when (selectedSection) {
                ConsoleSection.Users -> UsersTable(
                    state = users,
                    search = search,
                    roleFilter = roleFilter,
                    onView = { selectedUser = it; dialogMode = "view"; dialogTitle = "View user"; showDialog = true },
                    onEdit = { selectedUser = it; dialogMode = "edit"; dialogTitle = "Edit user"; showDialog = true },
                    onDelete = { deleteTargetUser = it },
                    onLockUnlock = { user, active ->
                        val tokenValue = token ?: return@UsersTable
                        userActionError = null
                        viewModel.setUserActive(tokenValue, user.id, active) {
                            reload(currentPage)
                        }
                    }
                )
                ConsoleSection.Services -> ServicesTable(
                    state = services,
                    categories = categories.items,
                    search = search,
                    onView = { selectedService = it; dialogMode = "view"; dialogTitle = "View service"; viewModel.loadCategories(token, page = 1, limit = 100); showDialog = true },
                    onEdit = { selectedService = it; dialogMode = "edit"; dialogTitle = "Edit service"; viewModel.loadCategories(token, page = 1, limit = 100); showDialog = true },
                    onDelete = { viewModel.deleteService(token ?: return@ServicesTable, it.id) { reload(currentPage) } }
                )
                ConsoleSection.Categories -> CategoriesTable(
                    state = categories,
                    search = search,
                    onView = { selectedCategory = it; dialogMode = "view"; dialogTitle = "View category"; showDialog = true },
                    onEdit = { selectedCategory = it; dialogMode = "edit"; dialogTitle = "Edit category"; showDialog = true },
                    onDelete = { viewModel.deleteCategory(token ?: return@CategoriesTable, it.id) { reload(currentPage) } }
                )
                ConsoleSection.Bookings -> BookingsTable(
                    state = bookings,
                    search = search,
                    statusFilter = statusFilter,
                    onView = { selectedBooking = it; dialogMode = "view"; dialogTitle = "View booking"; showDialog = true },
                    onEdit = { selectedBooking = it; dialogMode = "edit"; dialogTitle = "Update booking"; showDialog = true },
                    onConfirm = { viewModel.confirmBooking(token ?: return@BookingsTable, it.id, "Admin confirm") { reload(currentPage) } }
                )
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }

    if (showDialog) {
        when (selectedSection) {
            ConsoleSection.Users -> UserDialog(
                mode = dialogMode,
                value = selectedUser,
                onDismiss = { showDialog = false },
                onSave = { name, phone, email, role, active ->
                    val tokenValue = token ?: return@UserDialog
                    val user = selectedUser
                    userActionError = null
                    if (dialogMode == "create") {
                        viewModel.createUser(tokenValue, name, phone, email, role, active) {
                            reload(1)
                            showDialog = false
                        }
                    } else if (user != null) {
                        viewModel.updateUser(tokenValue, user.id, name, phone, email, role, active) {
                            reload(currentPage)
                            showDialog = false
                        }
                    }
                }
            )
            ConsoleSection.Services -> ServiceDialog(
                mode = dialogMode,
                value = selectedService,
                categories = categories.items,
                onDismiss = { showDialog = false },
                onSave = { categoryId, name, shortDescription, description, basePrice, estimatedMinutes, warrantyDays, active ->
                    val tokenValue = token ?: return@ServiceDialog
                    val service = selectedService
                    if (dialogMode == "create") {
                        viewModel.createService(tokenValue, categoryId, name, shortDescription, description, basePrice, estimatedMinutes, warrantyDays, active, onDone = { reload(1) })
                    } else if (service != null) {
                        viewModel.updateService(tokenValue, service.id, categoryId, name, shortDescription, description, basePrice, estimatedMinutes, warrantyDays, active, onDone = { reload(currentPage) })
                    }
                    showDialog = false
                }
            )
            ConsoleSection.Categories -> CategoryDialog(
                mode = dialogMode,
                value = selectedCategory,
                onDismiss = { showDialog = false },
                onSave = { name, description, active ->
                    val tokenValue = token ?: return@CategoryDialog
                    val category = selectedCategory
                    if (dialogMode == "create") {
                        viewModel.createCategory(tokenValue, name, description, active, onDone = { reload(1) })
                    } else if (category != null) {
                        viewModel.updateCategory(tokenValue, category.id, name, description, active, onDone = { reload(currentPage) })
                    }
                    showDialog = false
                }
            )
            ConsoleSection.Bookings -> BookingDialog(
                mode = dialogMode,
                value = selectedBooking,
                onDismiss = { showDialog = false },
                onSave = { status, note, scheduledAt ->
                    val tokenValue = token ?: return@BookingDialog
                    val booking = selectedBooking
                    if (booking != null) {
                        viewModel.updateBookingStatus(tokenValue, booking.id, status, note, scheduledAt) {
                            reload(currentPage)
                            showDialog = false
                        }
                    }
                }
            )
        }
    }

    if (deleteTargetUser != null) {
        AlertDialog(
            onDismissRequest = { deleteTargetUser = null },
            title = { Text("Delete user") },
            text = { Text("Bạn chắc chắn muốn xóa người dùng ${deleteTargetUser?.name ?: "này"}?") },
            confirmButton = {
                TextButton(onClick = {
                    val tokenValue = token ?: return@TextButton
                    val user = deleteTargetUser ?: return@TextButton
                    viewModel.deleteUser(tokenValue, user.id) { reload(currentPage) }
                    deleteTargetUser = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { deleteTargetUser = null }) { Text("Cancel") } }
        )
    }

    if (!userActionError.isNullOrBlank()) {
        AlertDialog(
            onDismissRequest = { userActionError = null },
            title = { Text("Thông báo") },
            text = { Text(userActionError.orEmpty()) },
            confirmButton = { TextButton(onClick = { userActionError = null }) { Text("OK") } }
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
    users: PageState<AdminUserDto>,
    services: PageState<RepairServiceDto>,
    categories: PageState<ServiceCategoryDto>,
    bookings: PageState<BookingSummaryDto>
) {
    Row(horizontalArrangement = Arrangement.spacedBy(13.dp), modifier = Modifier.fillMaxWidth()) {
        MiniStatCard("Users", "${users.items.size}", modifier = Modifier.weight(1f))
        MiniStatCard("Services", "${services.items.size}", modifier = Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(13.dp), modifier = Modifier.fillMaxWidth()) {
        MiniStatCard("Categories", "${categories.items.size}", modifier = Modifier.weight(1f))
        MiniStatCard("Bookings", "${bookings.items.size}", modifier = Modifier.weight(1f))
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
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        FilterChip(selected = selectedSection == ConsoleSection.Bookings, onClick = { onSelect(ConsoleSection.Bookings) }, label = { Text("Bookings") }, leadingIcon = { Icon(Icons.Default.Work, null) })
        FilterChip(selected = selectedSection == ConsoleSection.Categories, onClick = { onSelect(ConsoleSection.Categories) }, label = { Text("Categories") }, leadingIcon = { Icon(Icons.Default.Build, null) })
    }
}

@Composable
private fun AdminSectionToolbar(
    search: String,
    onSearchChange: (String) -> Unit,
    extraFilterLabel: String,
    onAdd: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
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
            Button(onClick = onAdd) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add")
            }
        }
    }
}

@Composable
private fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    pageSize: Int,
    onPageSizeChange: (Int) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        AssistChip(onClick = { onPageSizeChange(if (pageSize == 20) 50 else 20) }, label = { Text("Size: $pageSize") })
        Spacer(Modifier.width(8.dp))
        Text("Page $currentPage / $totalPages", color = Color(0xFFCBD5E1))
        Spacer(Modifier.weight(1f))
        Button(onClick = onPrev, enabled = currentPage > 1) { Text("Prev") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = onNext, enabled = currentPage < totalPages) { Text("Next") }
    }
}

@Composable
private fun UsersTable(
    state: PageState<AdminUserDto>,
    search: String,
    roleFilter: String?,
    onView: (AdminUserDto) -> Unit,
    onEdit: (AdminUserDto) -> Unit,
    onDelete: (AdminUserDto) -> Unit,
    onLockUnlock: (AdminUserDto, Boolean) -> Unit
) {
    val filtered = state.items.filter { (search.isBlank() || it.name.contains(search, true) || (it.email ?: "").contains(search, true)) && (roleFilter.isNullOrBlank() || it.role.equals(roleFilter, true)) }
    SectionTable("Users", listOf("Name", "Role", "Active", "Actions"), filtered, state.loading, state.error, "Chưa có users") { user ->
        TableRow(listOf(user.name, user.role, if (user.active) "Active" else "Locked"), onView = { onView(user) }, onEdit = { onEdit(user) }, onDelete = { onDelete(user) })
    }
}

@Composable
private fun ServicesTable(
    state: PageState<RepairServiceDto>,
    categories: List<ServiceCategoryDto>,
    search: String,
    onView: (RepairServiceDto) -> Unit,
    onEdit: (RepairServiceDto) -> Unit,
    onDelete: (RepairServiceDto) -> Unit
) {
    val categoryMap = remember(categories) { categories.associateBy { it.id } }
    val filtered = state.items.filter { search.isBlank() || it.name.contains(search, true) || (it.shortDescription ?: "").contains(search, true) }
    SectionTable("Services", listOf("Name", "Category", "Price", "Actions"), filtered, state.loading, state.error, "Chưa có services") { service ->
        TableRow(
            listOf(
                service.name,
                categoryMap[service.categoryId]?.name ?: "#${service.categoryId}",
                service.basePrice.toString()
            ),
            onView = { onView(service) },
            onEdit = { onEdit(service) },
            onDelete = { onDelete(service) }
        )
    }
}

@Composable
private fun CategoriesTable(
    state: PageState<ServiceCategoryDto>,
    search: String,
    onView: (ServiceCategoryDto) -> Unit,
    onEdit: (ServiceCategoryDto) -> Unit,
    onDelete: (ServiceCategoryDto) -> Unit
) {
    val filtered = state.items.filter { search.isBlank() || it.name.contains(search, true) || (it.description ?: "").contains(search, true) }
    SectionTable("Categories", listOf("Name", "Active", "Actions"), filtered, state.loading, state.error, "Chưa có categories") { category ->
        TableRow(listOf(category.name, if (category.active) "Active" else "Inactive"), onView = { onView(category) }, onEdit = { onEdit(category) }, onDelete = { onDelete(category) })
    }
}

@Composable
private fun BookingsTable(
    state: PageState<BookingSummaryDto>,
    search: String,
    statusFilter: String?,
    onView: (BookingSummaryDto) -> Unit,
    onEdit: (BookingSummaryDto) -> Unit,
    onConfirm: (BookingSummaryDto) -> Unit
) {
    val filtered = state.items.filter { (search.isBlank() || it.bookingCode.contains(search, true)) && (statusFilter.isNullOrBlank() || it.status.equals(statusFilter, true)) }
    SectionTable("Bookings", listOf("Code", "Status", "Price", "Actions"), filtered, state.loading, state.error, "Chưa có bookings") { booking ->
        TableRow(listOf(booking.bookingCode, booking.status, booking.totalEstimatedPrice.toString()), onView = { onView(booking) }, onEdit = { onEdit(booking) }, onDelete = { onConfirm(booking) })
    }
}

@Composable
private fun <T> SectionTable(title: String, headers: List<String>, items: List<T>, loading: Boolean, error: String?, emptyText: String, rowContent: @Composable (T) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
        when {
            loading -> Text("Đang tải...", color = Color(0xFFCBD5E1))
            !error.isNullOrBlank() -> Text(error, color = Color(0xFFFCA5A5))
            items.isEmpty() -> Text(emptyText, color = Color(0xFFCBD5E1))
            else -> Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth()) { headers.forEach { Text(it, color = Color(0xFF94A3B8), modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold) } }
                    items.forEach { rowContent(it) }
                }
            }
        }
    }
}

@Composable
private fun TableRow(cells: List<String>, onView: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        cells.forEach { Text(it, color = Color.White, modifier = Modifier.weight(1f)) }
        IconButton(onClick = onView) { Icon(Icons.Default.Visibility, null, tint = Color(0xFF38BDF8)) }
        IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Color(0xFF8B5CF6)) }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color(0xFFF87171)) }
    }
}

@Composable
private fun UserDialog(mode: String, value: AdminUserDto?, onDismiss: () -> Unit, onSave: (String, String, String, String, Boolean) -> Unit) {
    val name = remember(value) { mutableStateOf(value?.name.orEmpty()) }
    val phone = remember(value) { mutableStateOf(value?.phone.orEmpty()) }
    val email = remember(value) { mutableStateOf(value?.email.orEmpty()) }
    val role = remember(value) { mutableStateOf(value?.role.orEmpty()) }
    val active = remember(value) { mutableStateOf(value?.active ?: true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == "create") "Create user" else "Edit user") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name.value, { name.value = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(phone.value, { phone.value = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(email.value, { email.value = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(role.value, { role.value = it }, label = { Text("Role") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(active.value.toString(), { active.value = it.toBooleanStrictOrNull() ?: active.value }, label = { Text("Active true/false") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name.value, phone.value, email.value, role.value, active.value) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceDialog(mode: String, value: RepairServiceDto?, categories: List<ServiceCategoryDto>, onDismiss: () -> Unit, onSave: (Long, String, String, String, Long, Int, Int, Boolean) -> Unit) {
    val selectedCategoryId = remember(value, categories) {
        mutableStateOf(value?.categoryId ?: categories.firstOrNull()?.id ?: 0L)
    }
    val expanded = remember { mutableStateOf(false) }
    val name = remember(value) { mutableStateOf(value?.name.orEmpty()) }
    val shortDescription = remember(value) { mutableStateOf(value?.shortDescription.orEmpty()) }
    val description = remember(value) { mutableStateOf(value?.description.orEmpty()) }
    val basePrice = remember(value) { mutableStateOf(value?.basePrice?.toString().orEmpty()) }
    val estimatedMinutes = remember(value) { mutableStateOf(value?.estimatedMinutes?.toString().orEmpty()) }
    val warrantyDays = remember(value) { mutableStateOf(value?.warrantyDays?.toString().orEmpty()) }
    val active = remember(value) { mutableStateOf(value?.active ?: true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == "create") "Create service" else "Edit service") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        value = categories.firstOrNull { it.id == selectedCategoryId.value }?.name.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    selectedCategoryId.value = category.id
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(name.value, { name.value = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(shortDescription.value, { shortDescription.value = it }, label = { Text("Short description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(description.value, { description.value = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(basePrice.value, { basePrice.value = it }, label = { Text("Base price") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(estimatedMinutes.value, { estimatedMinutes.value = it }, label = { Text("Estimated minutes") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(warrantyDays.value, { warrantyDays.value = it }, label = { Text("Warranty days") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(active.value.toString(), { active.value = it.toBooleanStrictOrNull() ?: active.value }, label = { Text("Active true/false") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    selectedCategoryId.value,
                    name.value,
                    shortDescription.value,
                    description.value,
                    basePrice.value.toLongOrNull() ?: 0L,
                    estimatedMinutes.value.toIntOrNull() ?: 0,
                    warrantyDays.value.toIntOrNull() ?: 0,
                    active.value
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun CategoryDialog(mode: String, value: ServiceCategoryDto?, onDismiss: () -> Unit, onSave: (String, String, Boolean) -> Unit) {
    val name = remember(value) { mutableStateOf(value?.name.orEmpty()) }
    val description = remember(value) { mutableStateOf(value?.description.orEmpty()) }
    val active = remember(value) { mutableStateOf(value?.active ?: true) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(if (mode == "create") "Create category" else "Edit category") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(name.value, { name.value = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(description.value, { description.value = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(active.value.toString(), { active.value = it.toBooleanStrictOrNull() ?: active.value }, label = { Text("Active true/false") }, modifier = Modifier.fillMaxWidth())
    } }, confirmButton = { TextButton(onClick = { onSave(name.value, description.value, active.value) }) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingDialog(mode: String, value: BookingSummaryDto?, onDismiss: () -> Unit, onSave: (String, String, String?) -> Unit) {
    val status = remember(value) { mutableStateOf(value?.status.orEmpty().ifBlank { BookingStatusOptions.first() }) }
    val note = remember(value) { mutableStateOf("") }
    val scheduledDate = remember(value) { mutableStateOf(value?.scheduledAt.orEmpty().takeIf { it.isNotBlank() }?.toLocalDatePart() ?: currentDateInput()) }
    val scheduledTime = remember(value) { mutableStateOf(value?.scheduledAt.orEmpty().takeIf { it.isNotBlank() }?.toLocalTimePart() ?: currentTimeInput()) }
    val statusExpanded = remember { mutableStateOf(false) }
    val dateError = remember { mutableStateOf<String?>(null) }
    val timeError = remember { mutableStateOf<String?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == "create") "Create booking action" else "Update booking") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = statusExpanded.value,
                    onExpandedChange = { statusExpanded.value = !statusExpanded.value }
                ) {
                    OutlinedTextField(
                        value = status.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded.value) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded.value,
                        onDismissRequest = { statusExpanded.value = false }
                    ) {
                        BookingStatusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status.value = option
                                    statusExpanded.value = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(note.value, { note.value = it }, label = { Text("Note") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = scheduledDate.value,
                    onValueChange = {
                        scheduledDate.value = it
                        dateError.value = validateDateInput(it)
                    },
                    label = { Text("Scheduled date") },
                    placeholder = { Text("2026-06-07") },
                    supportingText = { Text("Format: yyyy-MM-dd") },
                    isError = dateError.value != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (!dateError.value.isNullOrBlank()) {
                    Text(dateError.value.orEmpty(), color = Color(0xFFFCA5A5))
                }
                OutlinedTextField(
                    value = scheduledTime.value,
                    onValueChange = {
                        scheduledTime.value = it
                        timeError.value = validateTimeInput(it)
                    },
                    label = { Text("Scheduled time") },
                    placeholder = { Text("14:30") },
                    supportingText = { Text("Format: HH:mm") },
                    isError = timeError.value != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (!timeError.value.isNullOrBlank()) {
                    Text(timeError.value.orEmpty(), color = Color(0xFFFCA5A5))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val scheduledAt = buildScheduledTimestamp(scheduledDate.value, scheduledTime.value)
                onSave(status.value, note.value, scheduledAt)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

private fun currentDateInput(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

private fun currentTimeInput(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

private fun validateDateInput(value: String): String? = runCatching { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).isLenient = false; SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(value) }.fold(onSuccess = { null }, onFailure = { "Ngày không hợp lệ" })

private fun validateTimeInput(value: String): String? {
    val regex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
    return if (regex.matches(value)) null else "Giờ không hợp lệ"
}

private fun buildScheduledTimestamp(date: String, time: String): String? {
    if (validateDateInput(date) != null || validateTimeInput(time) != null) return null
    return "$date $time:00"
}

private fun String.toLocalDatePart(): String = substringBefore('T').substringBefore(' ').takeIf { it.matches(Regex("^\\d{4}-\\d{2}-\\d{2}$")) }.orEmpty()

private fun String.toLocalTimePart(): String {
    val raw = substringAfter('T', missingDelimiterValue = this).substringAfter(' ', missingDelimiterValue = this)
    return raw.takeIf { it.length >= 5 }?.substring(0, 5).takeIf { !it.isNullOrBlank() }.orEmpty()
}
