package vn.vibe.booking.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.presentation.home.HomeViewModel

@Composable
fun MyBookingsScreen(
    viewModel: HomeViewModel,
    token: String?,
    initialFilterStatus: String? = null
) {
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        viewModel.loadMyBookings(token)
    }

    var reviewTarget by remember { mutableStateOf<BookingSummaryDto?>(null) }
    var rating by remember { mutableIntStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var reviewMessage by remember { mutableStateOf<String?>(null) }
    
    var detailTargetId by remember { mutableStateOf<Long?>(null) }
    var selectedFilter by remember { mutableStateOf(initialFilterStatus) }

    LaunchedEffect(initialFilterStatus) {
        if (initialFilterStatus != null) {
            selectedFilter = initialFilterStatus
        }
    }

    val statusFilters = listOf(
        null to "Tất cả",
        "PENDING" to "Chờ xác nhận",
        "CONFIRMED" to "Đã xác nhận",
        "IN_PROGRESS" to "Đang xử lý",
        "COMPLETED" to "Hoàn thành",
        "CANCELLED" to "Đã hủy"
    )

    val filteredBookings = remember(bookingsState.items, selectedFilter) {
        if (selectedFilter == null) bookingsState.items
        else bookingsState.items.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

    detailTargetId?.let { id ->
        BookingDetailScreen(
            bookingId = id,
            viewModel = viewModel,
            token = token,
            onBack = { detailTargetId = null }
        )
        return
    }

    reviewTarget?.let { target ->
        ReviewDialog(
            booking = target,
            rating = rating,
            onRatingChange = { rating = it },
            comment = comment,
            onCommentChange = { comment = it },
            isSubmitting = submitting,
            message = reviewMessage,
            onDismiss = { if (!submitting) reviewTarget = null },
            onSubmit = {
                val tokenValue = token ?: return@ReviewDialog
                submitting = true
                viewModel.createBookingReview(
                    token = tokenValue,
                    bookingId = target.id,
                    rating = rating,
                    comment = comment,
                    onDone = {
                        submitting = false
                        reviewTarget = null
                        reviewMessage = null
                    },
                    onError = {
                        submitting = false
                        reviewMessage = it
                    }
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Lịch sử đặt lịch",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            ScrollableTabRow(
                selectedTabIndex = statusFilters.indexOfFirst { it.first == selectedFilter }.coerceAtLeast(0),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                containerColor = Color.Transparent,
                divider = {}
            ) {
                statusFilters.forEach { (status, label) ->
                    Tab(
                        selected = selectedFilter == status,
                        onClick = { selectedFilter = status },
                        text = { Text(label, fontWeight = if (selectedFilter == status) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        when {
            bookingsState.loading -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            !bookingsState.error.isNullOrBlank() -> {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = bookingsState.error.orEmpty(),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
            filteredBookings.isEmpty() -> {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Không có đơn hàng nào.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                items(filteredBookings) { booking ->
                    BookingItemCard(
                        booking = booking,
                        onReviewClick = {
                            reviewTarget = booking
                            rating = 5
                            comment = ""
                            reviewMessage = null
                        },
                        onDetailClick = {
                            detailTargetId = booking.id
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(
    booking: BookingSummaryDto,
    onReviewClick: () -> Unit,
    onDetailClick: () -> Unit
) {
    val isCompleted = booking.status.equals("COMPLETED", ignoreCase = true) || booking.status.contains("HOÀN THÀNH", ignoreCase = true)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.bookingCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = booking.preferredDate.orEmpty().ifBlank { booking.preferredTimeSlot.orEmpty() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                BookingStatusBadge(status = booking.status)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tổng chi phí dự kiến", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${booking.totalEstimatedPrice} đ", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDetailClick,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text("Chi tiết")
                    }
                    if (isCompleted) {
                        Button(
                            onClick = onReviewClick,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đánh giá")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingStatusBadge(status: String) {
    val (containerColor, contentColor) = when {
        status.contains("COMPLETED", true) || status.contains("HOÀN", true) -> 
            Color(0xFFD1FAE5) to Color(0xFF065F46)
        status.contains("PROGRESS", true) || status.contains("ĐANG", true) -> 
            Color(0xFFFEF3C7) to Color(0xFF92400E)
        status.contains("CANCEL", true) || status.contains("HỦY", true) -> 
            Color(0xFFFEE2E2) to Color(0xFF991B1B)
        else -> 
            MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                if (status.contains("COMPLETED", true)) Icons.Default.CheckCircle else Icons.Default.Schedule,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = status,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ReviewDialog(
    booking: BookingSummaryDto,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    isSubmitting: Boolean,
    message: String?,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đánh giá dịch vụ") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Mã Booking: ${booking.bookingCode}", style = MaterialTheme.typography.bodyMedium)
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Số sao", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { onRatingChange(star) }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (star <= rating) Color(0xFFFBBF24) else MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    label = { Text("Nhận xét của bạn (tuỳ chọn)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                if (!message.isNullOrBlank()) {
                    Text(message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(onClick = onSubmit, enabled = !isSubmitting, shape = RoundedCornerShape(12.dp)) {
                Text(if (isSubmitting) "Đang gửi..." else "Gửi đánh giá")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
