package vn.vibe.booking.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import vn.vibe.booking.data.remote.BookingItemRequest
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.ServiceCategoryDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceBookingScreen(viewModel: HomeViewModel, token: String?, contentPadding: PaddingValues) {
    val services by viewModel.servicesState.collectAsStateWithLifecycle()
    val categories by viewModel.categoriesState.collectAsStateWithLifecycle()

    var search by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedServices by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }
    var submittedMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(token) {
        viewModel.loadCategories(token, page = 1, limit = 100)
        viewModel.loadServices(token, page = 1, limit = 100)
    }

    val selectedCategory = categories.items.firstOrNull { it.id == selectedCategoryId }
    val filteredServices = services.items.filter {
        (search.isBlank() || it.name.contains(search, true) || (it.shortDescription ?: "").contains(search, true)) &&
            (selectedCategoryId == null || it.categoryId == selectedCategoryId)
    }
    val selectedServiceItems = services.items.filter { selectedServices[it.id] != null }
    val totalEstimate = selectedServiceItems.sumOf { service -> service.basePrice * (selectedServices[service.id] ?: 1) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BookingHeaderCard(totalSelected = selectedServiceItems.size, totalEstimate = totalEstimate, onClear = { selectedServices = emptyMap() })
        }
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                placeholder = { Text("Tìm dịch vụ") },
                singleLine = true
            )
        }
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Danh mục", color = Color.White, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Tất cả",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { categoryExpanded = !categoryExpanded }) {
                                Icon(Icons.Default.ArrowDropDown, null)
                            }
                        }
                    )
                }
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Tất cả") }, onClick = { selectedCategoryId = null; categoryExpanded = false })
                    categories.items.forEach { category ->
                        DropdownMenuItem(text = { Text(category.name) }, onClick = { selectedCategoryId = category.id; categoryExpanded = false })
                    }
                }
            }
        }
        item { BookingHintCard() }
        item { Text("Danh sách dịch vụ", color = Color.White, fontWeight = FontWeight.SemiBold) }
        items(filteredServices) { service ->
            BookingServiceCard(
                service = service,
                categoryName = categories.items.firstOrNull { it.id == service.categoryId }?.name ?: "#${service.categoryId}",
                quantity = selectedServices[service.id] ?: 0,
                onAdd = { selectedServices = selectedServices + (service.id to ((selectedServices[service.id] ?: 0) + 1)) },
                onRemove = {
                    val current = selectedServices[service.id] ?: 0
                    selectedServices = if (current <= 1) selectedServices - service.id else selectedServices + (service.id to current - 1)
                }
            )
        }
        item {
            BookingSummarySection(
                items = selectedServiceItems,
                quantityMap = selectedServices,
                totalEstimate = totalEstimate,
                onSubmit = {
                    val tokenValue = token ?: return@BookingSummarySection
                    if (selectedServiceItems.isEmpty()) {
                        submittedMessage = "Hãy chọn ít nhất một dịch vụ"
                        return@BookingSummarySection
                    }
                    viewModel.createBooking(
                        token = tokenValue,
                        customerName = "Khách hàng",
                        customerPhone = "0000000000",
                        customerEmail = null,
                        deviceType = "Thiết bị",
                        deviceBrand = "",
                        deviceModel = "",
                        issueDescription = selectedServiceItems.joinToString { it.name },
                        preferredDate = "2026-05-30",
                        preferredTimeSlot = "09:00-11:00",
                        address = "",
                        note = "Đặt lịch từ app",
                        items = selectedServiceItems.map { BookingItemRequest(it.id, selectedServices[it.id] ?: 1) },
                        onDone = { submittedMessage = "Đã gửi yêu cầu đặt lịch thành công" }
                    )
                }
            )
        }
        if (!submittedMessage.isNullOrBlank()) {
            item { MessageBanner(submittedMessage.orEmpty()) }
        }
    }
}

@Composable
private fun BookingHeaderCard(totalSelected: Int, totalEstimate: Long, onClear: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Đặt lịch dịch vụ", color = Color(0xFF94A3B8))
            Text("Chọn dịch vụ và gửi yêu cầu đặt lịch", color = Color.White, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ConfirmationNumber, null, tint = Color(0xFF06B6D4))
                Spacer(Modifier.width(8.dp))
                Text("$totalSelected dịch vụ đã chọn", color = Color(0xFFCBD5E1))
                Spacer(Modifier.weight(1f))
                Text(totalEstimate.toString(), color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = onClear) { Text("Xoá lựa chọn") }
        }
    }
}

@Composable
private fun BookingHintCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Info, null, tint = Color(0xFF38BDF8)); Spacer(Modifier.width(8.dp)); Text("Mẹo đặt lịch", color = Color.White, fontWeight = FontWeight.SemiBold) }
            Text("Chọn danh mục bằng dropdown để lọc nhanh dịch vụ.", color = Color(0xFFCBD5E1))
        }
    }
}

@Composable
private fun BookingServiceCard(service: RepairServiceDto, categoryName: String, quantity: Int, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1220)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(service.name, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(categoryName, color = Color(0xFF94A3B8))
                }
                Icon(Icons.Default.LocalOffer, null, tint = Color(0xFF06B6D4))
            }
            Text(service.shortDescription ?: service.description ?: "Chưa có mô tả", color = Color(0xFFCBD5E1))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, null, tint = Color(0xFF8B5CF6))
                Spacer(Modifier.width(6.dp))
                Text("${service.estimatedMinutes} phút", color = Color(0xFFCBD5E1))
                Spacer(Modifier.weight(1f))
                Text(service.basePrice.toString(), color = Color.White, fontWeight = FontWeight.SemiBold)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onAdd) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(6.dp)); Text("Thêm") }
                Spacer(Modifier.width(10.dp))
                Button(onClick = onRemove, enabled = quantity > 0) { Text("Bớt") }
                Spacer(Modifier.weight(1f))
                Text("SL: $quantity", color = Color(0xFFCBD5E1))
            }
        }
    }
}

@Composable
private fun BookingSummarySection(items: List<RepairServiceDto>, quantityMap: Map<Long, Int>, totalEstimate: Long, onSubmit: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Tóm tắt booking", color = Color.White, fontWeight = FontWeight.Bold)
            if (items.isEmpty()) {
                Text("Chưa có dịch vụ nào được chọn.", color = Color(0xFF94A3B8))
            } else {
                items.forEach { service ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(service.name, color = Color.White, modifier = Modifier.weight(1f))
                        Text("x${quantityMap[service.id] ?: 1}", color = Color(0xFF94A3B8))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Tổng ước tính", color = Color(0xFFCBD5E1))
                Spacer(Modifier.weight(1f))
                Text(totalEstimate.toString(), color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
            }
            Button(onClick = onSubmit, modifier = Modifier.fillMaxWidth()) { Text("Gửi yêu cầu đặt lịch") }
        }
    }
}

@Composable
private fun MessageBanner(message: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF052E1A)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Text(message, color = Color(0xFF86EFAC), modifier = Modifier.padding(16.dp))
    }
}
