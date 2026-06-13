package vn.vibe.booking.presentation.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingManagementScreen(
    viewModel: HomeViewModel,
    token: String?,
    showTopBar: Boolean = true,
    initialFilterStatus: String? = null,
    onBack: () -> Unit = {}
) {
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
    val usersState by viewModel.usersState.collectAsStateWithLifecycle()

    var selectedBooking by remember { mutableStateOf<BookingSummaryDto?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showAssignDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf(initialFilterStatus) }

    LaunchedEffect(initialFilterStatus) {
        if (initialFilterStatus != null) {
            filterStatus = initialFilterStatus
        }
    }

    val statusFilters = listOf(null to "Tất cả", "PENDING" to "Chờ xử lý", "CONFIRMED" to "Đã xác nhận", "IN_PROGRESS" to "Đang sửa", "COMPLETED" to "Hoàn thành", "CANCELLED" to "Đã hủy")

    BackHandler(enabled = showTopBar, onBack = onBack)

    Scaffold(
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = { Text("Quản lý Booking") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở lại")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadBookings(token) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Status filter chips
            ScrollableTabRow(
                selectedTabIndex = statusFilters.indexOfFirst { it.first == filterStatus }.coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                divider = {}
            ) {
                statusFilters.forEachIndexed { index, (status, label) ->
                    Tab(
                        selected = filterStatus == status,
                        onClick = {
                            filterStatus = status
                            viewModel.loadBookings(token, status = status)
                        },
                        text = { Text(label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            HorizontalDivider()

            if (bookingsState.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (bookingsState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có booking nào", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookingsState.items) { booking ->
                        AdminBookingCard(
                            booking = booking,
                            onConfirm = { selectedBooking = booking; showConfirmDialog = true },
                            onAssign = { selectedBooking = booking; showAssignDialog = true },
                            onUpdateStatus = { selectedBooking = booking; showStatusDialog = true }
                        )
                    }
                }
            }
        }

        // Confirm Dialog
        if (showConfirmDialog) {
            selectedBooking?.let { sBooking ->
                var note by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Xác nhận Booking") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Mã: ${sBooking.bookingCode}")
                            OutlinedTextField(
                                value = note,
                                onValueChange = { note = it },
                                label = { Text("Ghi chú") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            token?.let {
                                viewModel.confirmBooking(it, sBooking.id, note) {
                                    showConfirmDialog = false
                                    viewModel.loadBookings(token, status = filterStatus)
                                }
                            }
                        }) { Text("Xác nhận") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) { Text("Hủy") }
                    }
                )
            }
        }

        // Assign Technician Dialog
        if (showAssignDialog) {
            selectedBooking?.let { sBooking ->
                var note by remember { mutableStateOf("") }
                var selectedTechnician by remember { mutableStateOf<AdminUserDto?>(null) }
                val technicians = usersState.items.filter { it.role.equals("TECHNICIAN", ignoreCase = true) && it.active }

                AlertDialog(
                    onDismissRequest = { showAssignDialog = false },
                    title = { Text("Phân công Kỹ thuật viên") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Mã: ${sBooking.bookingCode}", style = MaterialTheme.typography.bodyMedium)
                            
                            if (technicians.isEmpty()) {
                                Text("Không có kỹ thuật viên khả dụng", color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("Chọn kỹ thuật viên:", fontWeight = FontWeight.Medium)
                                technicians.forEach { tech ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedTechnician == tech,
                                            onClick = { selectedTechnician = tech }
                                        )
                                        Text("${tech.name} (${tech.phone ?: "N/A"})")
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = note,
                                onValueChange = { note = it },
                                label = { Text("Ghi chú") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                token?.let { t ->
                                    selectedTechnician?.let { tech ->
                                        viewModel.assignTechnician(t, sBooking.id, tech.id, note) {
                                            showAssignDialog = false
                                            viewModel.loadBookings(token, status = filterStatus)
                                        }
                                    }
                                }
                            },
                            enabled = selectedTechnician != null
                        ) { Text("Phân công") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAssignDialog = false }) { Text("Hủy") }
                    }
                )
            }
        }

        // Update Status Dialog
        if (showStatusDialog) {
            selectedBooking?.let { sBooking ->
                var note by remember { mutableStateOf("") }
                var newStatus by remember { mutableStateOf("") }
                val statuses = listOf("CONFIRMED", "IN_PROGRESS", "COMPLETED", "CANCELLED")

                AlertDialog(
                    onDismissRequest = { showStatusDialog = false },
                    title = { Text("Cập nhật trạng thái") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Mã: ${sBooking.bookingCode}")
                            Text("Trạng thái hiện tại: ${sBooking.status}")
                            
                            Text("Trạng thái mới:", fontWeight = FontWeight.Medium)
                            statuses.forEach { status ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = newStatus == status,
                                        onClick = { newStatus = status }
                                    )
                                    Text(status)
                                }
                            }
                            OutlinedTextField(
                                value = note,
                                onValueChange = { note = it },
                                label = { Text("Ghi chú") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                token?.let { t ->
                                    viewModel.updateBookingStatus(t, sBooking.id, newStatus, note, null) {
                                        showStatusDialog = false
                                        viewModel.loadBookings(token, status = filterStatus)
                                    }
                                }
                            },
                            enabled = newStatus.isNotBlank()
                        ) { Text("Cập nhật") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStatusDialog = false }) { Text("Hủy") }
                    }
                )
            }
        }
    }
}

@Composable
fun AdminBookingCard(
    booking: BookingSummaryDto,
    onConfirm: () -> Unit,
    onAssign: () -> Unit,
    onUpdateStatus: () -> Unit
) {
    val isPending = booking.status.equals("PENDING", ignoreCase = true) || booking.status.contains("CHỜ", ignoreCase = true)
    val isConfirmed = booking.status.equals("CONFIRMED", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isPending) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
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
                Text(booking.bookingCode, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                StatusChip(booking.status)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                booking.preferredDate?.let {
                    Text("📅 $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                booking.preferredTimeSlot?.let {
                    Text("🕐 $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text(
                "Tổng: ${booking.totalEstimatedPrice} đ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPending) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xác nhận", style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (isPending || isConfirmed) {
                    OutlinedButton(
                        onClick = onAssign,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Phân công", style = MaterialTheme.typography.labelMedium)
                    }
                }
                TextButton(
                    onClick = onUpdateStatus,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text("Đổi TT", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, label) = when {
        status.equals("PENDING", ignoreCase = true) || status.contains("CHỜ", ignoreCase = true) ->
            MaterialTheme.colorScheme.error to "Chờ xử lý"
        status.equals("CONFIRMED", ignoreCase = true) ->
            MaterialTheme.colorScheme.tertiary to "Đã xác nhận"
        status.equals("IN_PROGRESS", ignoreCase = true) ->
            MaterialTheme.colorScheme.primary to "Đang sửa"
        status.equals("COMPLETED", ignoreCase = true) || status.contains("HOÀN THÀNH", ignoreCase = true) ->
            MaterialTheme.colorScheme.secondary to "Hoàn thành"
        status.equals("CANCELLED", ignoreCase = true) ->
            MaterialTheme.colorScheme.outline to "Đã hủy"
        else -> MaterialTheme.colorScheme.onSurfaceVariant to status
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        ),
        border = null
    )
}
