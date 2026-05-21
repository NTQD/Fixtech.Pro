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

private enum class ConsoleSection { Users, Services, Categories, Bookings }

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

    fun reload(page: Int = 1) {
        when (selectedSection) {
            ConsoleSection.Users -> viewModel.loadUsers(token, search.takeIf { it.isNotBlank() }, roleFilter, null, page, pageSize)
            ConsoleSection.Services -> viewModel.loadServices(token, search.takeIf { it.isNotBlank() }, null, page, pageSize)
            ConsoleSection.Categories -> viewModel.loadCategories(token, search.takeIf { it.isNotBlank() }, page, pageSize)
            ConsoleSection.Bookings -> viewModel.loadBookings(token, search.takeIf { it.isNotBlank() }, statusFilter, null, null, page, pageSize)
        }
    }

    LaunchedEffect(token, selectedSection, pageSize) { reload(1) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ConsoleHeader() }
        item { ConsoleSummaryGrid(users, services, categories, bookings) }
        item { ConsoleShortcuts(selectedSection) { selectedSection = it; search = ""; roleFilter = null; statusFilter = null } }
        item {
            AdminSectionToolbar(
                search = search,
                onSearchChange = { search = it; reload(1) },
                pageSize = pageSize,
                onPageSizeChange = { pageSize = it; reload(1) },
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
                        ConsoleSection.Bookings -> "Create booking action"
                    }
                    showDialog = true
                },
                onPrev = { val current = currentPage(selectedSection, users, services, categories, bookings); if (current > 1) reload(current - 1) },
                onNext = { val current = currentPage(selectedSection, users, services, categories, bookings); reload(current + 1) }
            )
        }
        item {
            when (selectedSection) {
                ConsoleSection.Users -> UsersTable(users, search, roleFilter, onView = { selectedUser = it; dialogMode = "view"; dialogTitle = "View user"; showDialog = true }, onEdit = { selectedUser = it; dialogMode = "edit"; dialogTitle = "Edit user"; showDialog = true }, onDelete = { viewModel.setUserActive(token ?: return@UsersTable, it.id, false) { reload(currentPage(selectedSection, users, services, categories, bookings)) } }, onLockUnlock = { user, active -> viewModel.setUserActive(token ?: return@UsersTable, user.id, active) { reload(currentPage(selectedSection, users, services, categories, bookings)) } })
                ConsoleSection.Services -> ServicesTable(services, search, onView = { selectedService = it; dialogMode = "view"; dialogTitle = "View service"; showDialog = true }, onEdit = { selectedService = it; dialogMode = "edit"; dialogTitle = "Edit service"; showDialog = true }, onDelete = { viewModel.deleteService(token ?: return@ServicesTable, it.id) { reload(currentPage(selectedSection, users, services, categories, bookings)) } })
                ConsoleSection.Categories -> CategoriesTable(categories, search, onView = { selectedCategory = it; dialogMode = "view"; dialogTitle = "View category"; showDialog = true }, onEdit = { selectedCategory = it; dialogMode = "edit"; dialogTitle = "Edit category"; showDialog = true }, onDelete = { viewModel.deleteCategory(token ?: return@CategoriesTable, it.id) { reload(currentPage(selectedSection, users, services, categories, bookings)) } })
                ConsoleSection.Bookings -> BookingsTable(bookings, search, statusFilter, onView = { selectedBooking = it; dialogMode = "view"; dialogTitle = "View booking"; showDialog = true }, onEdit = { selectedBooking = it; dialogMode = "edit"; dialogTitle = "Update booking"; showDialog = true }, onConfirm = { viewModel.confirmBooking(token ?: return@BookingsTable, it.id, "Admin confirm") { reload(currentPage(selectedSection, users, services, categories, bookings)) } })
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }

    if (showDialog) {
        CrudDialog(
            title = dialogTitle,
            mode = dialogMode,
            user = selectedUser,
            service = selectedService,
            category = selectedCategory,
            booking = selectedBooking,
            onDismiss = { showDialog = false },
            onSave = { payload ->
                when (selectedSection) {
                    ConsoleSection.Users -> {
                        val u = selectedUser
                        if (dialogMode == "create") viewModel.updateUser(token ?: "", 0, payload[0], payload[1], payload[2], payload[3], payload[4].toBooleanStrictOrNull(), onDone = { reload(1) })
                        else if (u != null) viewModel.updateUser(token ?: "", u.id, payload[0], payload[1], payload[2], payload[3], payload[4].toBooleanStrictOrNull(), onDone = { reload(currentPage(selectedSection, users, services, categories, bookings)) })
                    }
                    ConsoleSection.Services -> {
                        val s = selectedService
                        if (dialogMode == "create") viewModel.createService(token ?: "", payload[0].toLong(), payload[1], payload[2], payload[3], payload[4].toLong(), payload[5].toInt(), payload[6].toInt(), payload[7].toBooleanStrictOrNull() == true, onDone = { reload(1) })
                        else if (s != null) viewModel.updateService(token ?: "", s.id, payload[0].toLong(), payload[1], payload[2], payload[3], payload[4].toLong(), payload[5].toInt(), payload[6].toInt(), payload[7].toBooleanStrictOrNull() == true, onDone = { reload(currentPage(selectedSection, users, services, categories, bookings)) })
                    }
                    ConsoleSection.Categories -> {
                        val c = selectedCategory
                        if (dialogMode == "create") viewModel.createCategory(token ?: "", payload[0], payload[1], payload[2].toBooleanStrictOrNull() == true, onDone = { reload(1) })
                        else if (c != null) viewModel.updateCategory(token ?: "", c.id, payload[0], payload[1], payload[2].toBooleanStrictOrNull(), onDone = { reload(currentPage(selectedSection, users, services, categories, bookings)) })
                    }
                    ConsoleSection.Bookings -> {
                        val b = selectedBooking
                        if (b != null) viewModel.updateBookingStatus(token ?: "", b.id, payload[0], payload[1], onDone = { reload(currentPage(selectedSection, users, services, categories, bookings)) })
                    }
                }
                showDialog = false
            }
        )
    }
}

private fun currentPage(section: ConsoleSection, users: PageState<AdminUserDto>, services: PageState<RepairServiceDto>, categories: PageState<ServiceCategoryDto>, bookings: PageState<BookingSummaryDto>): Int = when (section) {
    ConsoleSection.Users -> users.page
    ConsoleSection.Services -> services.page
    ConsoleSection.Categories -> categories.page
    ConsoleSection.Bookings -> bookings.page
}

@Composable private fun ConsoleHeader() { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("Admin Console", color = Color(0xFF94A3B8)); Text("Quản lý booking, dịch vụ và người dùng", color = Color.White, fontWeight = FontWeight.Bold); Text("Tìm kiếm, lọc, xem dạng bảng và thao tác CRUD ngay trong từng màn.", color = Color(0xFFCBD5E1)) } } }
@Composable private fun ConsoleSummaryGrid(users: PageState<AdminUserDto>, services: PageState<RepairServiceDto>, categories: PageState<ServiceCategoryDto>, bookings: PageState<BookingSummaryDto>) { Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) { MiniStatCard("Users", "${users.items.size}", modifier = Modifier.weight(1f)); MiniStatCard("Services", "${services.items.size}", modifier = Modifier.weight(1f)) }; Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) { MiniStatCard("Categories", "${categories.items.size}", modifier = Modifier.weight(1f)); MiniStatCard("Bookings", "${bookings.items.size}", modifier = Modifier.weight(1f)) } }
@Composable private fun MiniStatCard(title: String, value: String, modifier: Modifier = Modifier) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), modifier = modifier) { Column(Modifier.padding(14.dp)) { Text(title, color = Color(0xFF94A3B8)); Text(value, color = Color.White, fontWeight = FontWeight.Bold) } } }
@Composable private fun ConsoleShortcuts(selectedSection: ConsoleSection, onSelect: (ConsoleSection) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()) {
        FilterChip(selected = selectedSection == ConsoleSection.Users,
            onClick = { onSelect(ConsoleSection.Users) },
            label = { Text("Users") },
            leadingIcon = { Icon(Icons.Default.People, null) });
        FilterChip(selected = selectedSection == ConsoleSection.Services,
            onClick = { onSelect(ConsoleSection.Services) },
            label = { Text("Services") },
            leadingIcon = { Icon(Icons.Default.Category, null) });
        FilterChip(selected = selectedSection == ConsoleSection.Bookings,
            onClick = { onSelect(ConsoleSection.Bookings) },
            label = { Text("Bookings") },
            leadingIcon = { Icon(Icons.Default.Work, null) })
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()) {
        FilterChip(selected = selectedSection == ConsoleSection.Categories,
            onClick = { onSelect(ConsoleSection.Categories) },
            label = { Text("Categories") },
            leadingIcon = { Icon(Icons.Default.Build, null) })
    }
}
@Composable private fun AdminSectionToolbar(
    search: String,
    onSearchChange: (String) -> Unit,
    pageSize: Int,
    onPageSizeChange: (Int) -> Unit,
    extraFilterLabel: String,
    onAdd: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(value = search, onValueChange = onSearchChange, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search by keyword") },
            singleLine = true);
        Row(
            verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth())
        {
            AssistChip(onClick = {},
        label = { Text(extraFilterLabel) },
        leadingIcon = { Icon(Icons.Default.FilterAlt, null) });
        Spacer(Modifier.width(8.dp));
        AssistChip(onClick = { onPageSizeChange(if (pageSize == 20) 50 else 20) }, label = { Text("Size: $pageSize") });
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth())
        {
            Spacer(Modifier.weight(1f)); Button(onClick = onPrev) { Text("Prev") };
            Spacer(Modifier.width(8.dp)); Button(onClick = onNext) { Text("Next") };
            Spacer(Modifier.width(8.dp)); Button(onClick = onAdd) { Icon(Icons.Default.Add, null);
            Spacer(Modifier.width(8.dp)); Text("Add") } }
    }

}
@Composable private fun UsersTable(state: PageState<AdminUserDto>, search: String, roleFilter: String?, onView: (AdminUserDto) -> Unit, onEdit: (AdminUserDto) -> Unit, onDelete: (AdminUserDto) -> Unit, onLockUnlock: (AdminUserDto, Boolean) -> Unit) { val filtered = state.items.filter { (search.isBlank() || it.name.contains(search, true) || (it.email ?: "").contains(search, true)) && (roleFilter.isNullOrBlank() || it.role.equals(roleFilter, true)) }; SectionTable("Users", listOf("Name", "Role", "Active", "Actions"), filtered, state.loading, state.error, "Chưa có users") { user -> TableRow(listOf(user.name, user.role, if (user.active) "Active" else "Locked"), onView = { onView(user) }, onEdit = { onEdit(user) }, onDelete = { onDelete(user) }) } }
@Composable private fun ServicesTable(state: PageState<RepairServiceDto>, search: String, onView: (RepairServiceDto) -> Unit, onEdit: (RepairServiceDto) -> Unit, onDelete: (RepairServiceDto) -> Unit) { val filtered = state.items.filter { search.isBlank() || it.name.contains(search, true) || (it.shortDescription ?: "").contains(search, true) }; SectionTable("Services", listOf("Name", "Category", "Price", "Actions"), filtered, state.loading, state.error, "Chưa có services") { service -> TableRow(listOf(service.name, service.categoryId.toString(), service.basePrice.toString()), onView = { onView(service) }, onEdit = { onEdit(service) }, onDelete = { onDelete(service) }) } }
@Composable private fun CategoriesTable(state: PageState<ServiceCategoryDto>, search: String, onView: (ServiceCategoryDto) -> Unit, onEdit: (ServiceCategoryDto) -> Unit, onDelete: (ServiceCategoryDto) -> Unit) { val filtered = state.items.filter { search.isBlank() || it.name.contains(search, true) || (it.description ?: "").contains(search, true) }; SectionTable("Categories", listOf("Name", "Active", "Actions"), filtered, state.loading, state.error, "Chưa có categories") { category -> TableRow(listOf(category.name, if (category.active) "Active" else "Inactive"), onView = { onView(category) }, onEdit = { onEdit(category) }, onDelete = { onDelete(category) }) } }
@Composable private fun BookingsTable(state: PageState<BookingSummaryDto>, search: String, statusFilter: String?, onView: (BookingSummaryDto) -> Unit, onEdit: (BookingSummaryDto) -> Unit, onConfirm: (BookingSummaryDto) -> Unit) { val filtered = state.items.filter { (search.isBlank() || it.bookingCode.contains(search, true)) && (statusFilter.isNullOrBlank() || it.status.equals(statusFilter, true)) }; SectionTable("Bookings", listOf("Code", "Status", "Price", "Actions"), filtered, state.loading, state.error, "Chưa có bookings") { booking -> TableRow(listOf(booking.bookingCode, booking.status, booking.totalEstimatedPrice.toString()), onView = { onView(booking) }, onEdit = { onEdit(booking) }, onDelete = { onConfirm(booking) }) } }
@Composable private fun <T> SectionTable(title: String, headers: List<String>, items: List<T>, loading: Boolean, error: String?, emptyText: String, rowContent: @Composable (T) -> Unit) { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(title, color = Color.White, fontWeight = FontWeight.SemiBold); when { loading -> Text("Đang tải...", color = Color(0xFFCBD5E1)); !error.isNullOrBlank() -> Text(error, color = Color(0xFFFCA5A5)); items.isEmpty() -> Text(emptyText, color = Color(0xFFCBD5E1)); else -> Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Row(Modifier.fillMaxWidth()) { headers.forEach { Text(it, color = Color(0xFF94A3B8), modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold) } }; items.forEach { rowContent(it) } } } } } }
@Composable private fun TableRow(cells: List<String>, onView: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) { cells.forEach { Text(it, color = Color.White, modifier = Modifier.weight(1f)) }; IconButton(onClick = onView) { Icon(Icons.Default.Visibility, null, tint = Color(0xFF38BDF8)) }; IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Color(0xFF8B5CF6)) }; IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color(0xFFF87171)) } } }
@Composable private fun CrudDialog(title: String, mode: String, user: AdminUserDto?, service: RepairServiceDto?, category: ServiceCategoryDto?, booking: BookingSummaryDto?, onDismiss: () -> Unit, onSave: (List<String>) -> Unit) { val fields = remember { mutableStateOf(listOf("", "", "", "", "", "", "", "")) }; AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { Text("Mode: $mode"); OutlinedTextField(value = fields.value.getOrElse(0) { "" }, onValueChange = { v -> fields.value = fields.value.toMutableList().also { it[0] = v } }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Field 1") }); OutlinedTextField(value = fields.value.getOrElse(1) { "" }, onValueChange = { v -> fields.value = fields.value.toMutableList().also { it[1] = v } }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Field 2") }); OutlinedTextField(value = fields.value.getOrElse(2) { "" }, onValueChange = { v -> fields.value = fields.value.toMutableList().also { it[2] = v } }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Field 3") }) } }, confirmButton = { TextButton(onClick = { onSave(fields.value) }) { Text("Save") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }) }
