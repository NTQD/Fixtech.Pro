package vn.vibe.booking.presentation.technician

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.data.remote.BookingDetailDto
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.presentation.admin.StatusChip
import vn.vibe.booking.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechBookingListScreen(
    viewModel: HomeViewModel,
    token: String?,
    onBack: () -> Unit
) {
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
    val bookingDetailState by viewModel.bookingDetailState.collectAsStateWithLifecycle()

    var selectedBookingId by remember { mutableStateOf<Long?>(null) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var filterStatus by remember { mutableStateOf<String?>(null) }

    val statusFilters = listOf(
        null to "Tất cả",
        "CONFIRMED" to "Chờ nhận",
        "IN_PROGRESS" to "Đang sửa",
        "COMPLETED" to "Hoàn thành"
    )

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Công việc của tôi") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadMyBookings(token, status = filterStatus) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Tải lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Status filter
            ScrollableTabRow(
                selectedTabIndex = statusFilters.indexOfFirst { it.first == filterStatus }.coerceAtLeast(0),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp,
                divider = {}
            ) {
                statusFilters.forEach { (status, label) ->
                    Tab(
                        selected = filterStatus == status,
                        onClick = {
                            filterStatus = status
                            viewModel.loadMyBookings(token, status = status)
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Chưa có công việc nào", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookingsState.items) { booking ->
                        TechBookingCard(
                            booking = booking,
                            onStartWork = {
                                selectedBookingId = booking.id
                                showStatusDialog = true
                            },
                            onAddNote = {
                                selectedBookingId = booking.id
                                showNoteDialog = true
                            },
                            onViewDetail = {
                                selectedBookingId = booking.id
                                viewModel.loadBookingDetail(token, booking.id)
                                showDetailSheet = true
                            }
                        )
                    }
                }
            }
        }

        // Update Status Dialog
        if (showStatusDialog && selectedBookingId != null) {
            var newStatus by remember { mutableStateOf("") }
            var note by remember { mutableStateOf("") }
            val statuses = listOf(
                "IN_PROGRESS" to "Bắt đầu sửa",
                "COMPLETED" to "Hoàn thành"
            )

            AlertDialog(
                onDismissRequest = { showStatusDialog = false },
                title = { Text("Cập nhật trạng thái") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        statuses.forEach { (status, label) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = newStatus == status,
                                    onClick = { newStatus = status }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label)
                            }
                        }
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Ghi chú (tùy chọn)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            token?.let { t ->
                                selectedBookingId?.let { id ->
                                    viewModel.updateBookingStatus(t, id, newStatus, note, null) {
                                        showStatusDialog = false
                                        viewModel.loadMyBookings(token, status = filterStatus)
                                    }
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

        // Add Note Dialog
        if (showNoteDialog && selectedBookingId != null) {
            var note by remember { mutableStateOf("") }
            var errorMsg by remember { mutableStateOf<String?>(null) }

            AlertDialog(
                onDismissRequest = { showNoteDialog = false },
                title = { Text("Thêm ghi chú") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it; errorMsg = null },
                            label = { Text("Nội dung ghi chú") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        errorMsg?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            token?.let { t ->
                                selectedBookingId?.let { id ->
                                    viewModel.addBookingNote(t, id, note,
                                        onDone = { showNoteDialog = false },
                                        onError = { msg -> errorMsg = msg }
                                    )
                                }
                            }
                        },
                        enabled = note.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gửi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoteDialog = false }) { Text("Hủy") }
                }
            )
        }

        // Detail Bottom Sheet
        if (showDetailSheet && selectedBookingId != null) {
            AlertDialog(
                onDismissRequest = { showDetailSheet = false },
                title = { Text("Chi tiết Booking") },
                text = {
                    when (val state = bookingDetailState) {
                        is UiState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.Success -> {
                            BookingDetailContent(state.data)
                        }
                        is UiState.Error -> {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                        }
                        else -> {}
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDetailSheet = false }) { Text("Đóng") }
                }
            )
        }
    }
}

@Composable
fun BookingDetailContent(booking: BookingDetailDto) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Mã: ${booking.bookingCode}", fontWeight = FontWeight.Bold)
        Text("KH: ${booking.customerName} - ${booking.customerPhone}")
        Text("Thiết bị: ${booking.deviceType}")
        Text("Lỗi: ${booking.issueDescription}")
        
        HorizontalDivider()
        Text("Dịch vụ:", fontWeight = FontWeight.Medium)
        booking.items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("• ${item.serviceName}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                Text("${item.estimatedPrice}đ", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        if (booking.statusHistory.isNotEmpty()) {
            HorizontalDivider()
            Text("Lịch sử:", fontWeight = FontWeight.Medium)
            booking.statusHistory.forEach { h ->
                Text(
                    "• ${h.fromStatus ?: "—"} → ${h.toStatus}${h.note?.let { " ($it)" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TechBookingCard(
    booking: BookingSummaryDto,
    onStartWork: () -> Unit,
    onAddNote: () -> Unit,
    onViewDetail: () -> Unit
) {
    val isActive = !booking.status.equals("COMPLETED", ignoreCase = true) &&
            !booking.status.equals("CANCELLED", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 2.dp else 0.dp),
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
                if (isActive) {
                    Button(
                        onClick = onStartWork,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            if (booking.status.equals("IN_PROGRESS", ignoreCase = true)) Icons.Default.Check else Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (booking.status.equals("IN_PROGRESS", ignoreCase = true)) "Hoàn thành" else "Bắt đầu",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    OutlinedButton(
                        onClick = onAddNote,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ghi chú", style = MaterialTheme.typography.labelMedium)
                    }
                }
                TextButton(
                    onClick = onViewDetail,
                    modifier = if (!isActive) Modifier.fillMaxWidth() else Modifier
                ) {
                    Text("Chi tiết", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
