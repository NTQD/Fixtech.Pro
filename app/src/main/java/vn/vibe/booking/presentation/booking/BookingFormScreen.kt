package vn.vibe.booking.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.presentation.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormScreen(
    formViewModel: BookingFormViewModel,
    homeViewModel: HomeViewModel,
    token: String?,
    initialService: RepairServiceDto? = null,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val state by formViewModel.state.collectAsStateWithLifecycle()
    val servicesState by homeViewModel.servicesState.collectAsStateWithLifecycle()
    
    LaunchedEffect(initialService) {
        if (initialService != null && state.selectedServices.isEmpty()) {
            formViewModel.addService(initialService)
        }
    }
    
    LaunchedEffect(state.submitSuccess) {
        if (state.submitSuccess) {
            formViewModel.resetState()
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt Lịch Sửa Chữa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở lại")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Stepper(
                currentStep = state.currentStep,
                steps = listOf("Thông tin", "Thiết bị", "Dịch vụ", "Thời gian", "Xác nhận"),
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )

            HorizontalDivider()

            Box(modifier = Modifier.weight(1f)) {
                when (state.currentStep) {
                    0 -> CustomerInfoStep(state, formViewModel)
                    1 -> DeviceInfoStep(state, formViewModel)
                    2 -> ServicesStep(state, formViewModel, servicesState.items)
                    3 -> ScheduleInfoStep(state, formViewModel)
                    4 -> ConfirmationStep(state, formViewModel, token)
                }
            }
        }
    }
}

@Composable
fun Stepper(currentStep: Int, steps: List<String>, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, stepLabel ->
            val isActive = index == currentStep
            val isCompleted = index < currentStep
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> MaterialTheme.colorScheme.primary
                                isActive -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                    } else {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stepLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive || isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CustomerInfoStep(state: BookingFormState, viewModel: BookingFormViewModel) {
    var name by remember { mutableStateOf(state.customerName) }
    var phone by remember { mutableStateOf(state.customerPhone) }
    var email by remember { mutableStateOf(state.customerEmail) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Thông tin liên hệ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; viewModel.updateCustomerInfo(name, phone, email) },
            label = { Text("Họ và tên *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; viewModel.updateCustomerInfo(name, phone, email) },
            label = { Text("Số điện thoại *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; viewModel.updateCustomerInfo(name, phone, email) },
            label = { Text("Email (Tùy chọn)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { viewModel.setStep(1) },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && phone.isNotBlank()
        ) {
            Text("Tiếp tục")
        }
    }
}

@Composable
fun DeviceInfoStep(state: BookingFormState, viewModel: BookingFormViewModel) {
    var type by remember { mutableStateOf(state.deviceType) }
    var brand by remember { mutableStateOf(state.deviceBrand) }
    var model by remember { mutableStateOf(state.deviceModel) }
    var issue by remember { mutableStateOf(state.issueDescription) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Thông tin thiết bị", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        OutlinedTextField(
            value = type,
            onValueChange = { type = it; viewModel.updateDeviceInfo(type, brand, model, issue) },
            label = { Text("Loại thiết bị (vd: Laptop, Điện thoại) *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it; viewModel.updateDeviceInfo(type, brand, model, issue) },
                label = { Text("Hãng *") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = model,
                onValueChange = { model = it; viewModel.updateDeviceInfo(type, brand, model, issue) },
                label = { Text("Model *") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        OutlinedTextField(
            value = issue,
            onValueChange = { issue = it; viewModel.updateDeviceInfo(type, brand, model, issue) },
            label = { Text("Mô tả lỗi đang gặp phải *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(modifier = Modifier.weight(1f))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { viewModel.setStep(0) }, modifier = Modifier.weight(1f)) {
                Text("Quay lại")
            }
            Button(
                onClick = { viewModel.setStep(2) },
                modifier = Modifier.weight(1f),
                enabled = type.isNotBlank() && brand.isNotBlank() && model.isNotBlank() && issue.isNotBlank()
            ) {
                Text("Tiếp tục")
            }
        }
    }
}

@Composable
fun ServicesStep(state: BookingFormState, viewModel: BookingFormViewModel, availableServices: List<RepairServiceDto>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Dịch vụ sửa chữa", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (state.selectedServices.isNotEmpty()) {
            Text("Đã chọn (${state.selectedServices.size}):", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            state.selectedServices.forEach { s ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(s.name, modifier = Modifier.weight(1f))
                    TextButton(onClick = { viewModel.removeService(s.id) }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        Text("Chọn thêm dịch vụ:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        // LazyColumn for remaining services
        // Ideally we would show a bottom sheet or a search bar, but for simplicity we list them here
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            availableServices.filter { s -> !state.selectedServices.any { it.id == s.id } }.forEach { s ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    onClick = { viewModel.addService(s) }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(s.name, fontWeight = FontWeight.Medium)
                        Text("${s.basePrice} đ", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { viewModel.setStep(1) }, modifier = Modifier.weight(1f)) { Text("Quay lại") }
            Button(
                onClick = { viewModel.setStep(3) },
                modifier = Modifier.weight(1f),
                enabled = state.selectedServices.isNotEmpty()
            ) {
                Text("Tiếp tục")
            }
        }
    }
}

@Composable
fun ScheduleInfoStep(state: BookingFormState, viewModel: BookingFormViewModel) {
    var date by remember { mutableStateOf(state.preferredDate) }
    var timeSlot by remember { mutableStateOf(state.preferredTimeSlot) }
    var address by remember { mutableStateOf(state.address) }
    var note by remember { mutableStateOf(state.note) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Thời gian & Địa điểm", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = date,
                onValueChange = { date = it; viewModel.updateScheduleInfo(date, timeSlot, address, note) },
                label = { Text("Ngày (dd/mm/yyyy) *") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = timeSlot,
                onValueChange = { timeSlot = it; viewModel.updateScheduleInfo(date, timeSlot, address, note) },
                label = { Text("Giờ (vd: 09:00) *") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        OutlinedTextField(
            value = address,
            onValueChange = { address = it; viewModel.updateScheduleInfo(date, timeSlot, address, note) },
            label = { Text("Địa chỉ lấy máy/Sửa tại nhà *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it; viewModel.updateScheduleInfo(date, timeSlot, address, note) },
            label = { Text("Ghi chú thêm (Tùy chọn)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.weight(1f))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { viewModel.setStep(2) }, modifier = Modifier.weight(1f)) {
                Text("Quay lại")
            }
            Button(
                onClick = { viewModel.setStep(4) },
                modifier = Modifier.weight(1f),
                enabled = date.isNotBlank() && timeSlot.isNotBlank() && address.isNotBlank()
            ) {
                Text("Tiếp tục")
            }
        }
    }
}

@Composable
fun ConfirmationStep(state: BookingFormState, viewModel: BookingFormViewModel, token: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Xác nhận thông tin", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Khách hàng: ${state.customerName} - ${state.customerPhone}")
                Text("Thiết bị: ${state.deviceBrand} ${state.deviceModel} (${state.deviceType})")
                Text("Lỗi: ${state.issueDescription}")
                Text("Thời gian: ${state.preferredTimeSlot} ngày ${state.preferredDate}")
                Text("Địa chỉ: ${state.address}")
            }
        }

        Text("Dịch vụ", fontWeight = FontWeight.Bold)
        state.selectedServices.forEach { s ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(s.name)
                Text("${s.basePrice} đ")
            }
        }
        HorizontalDivider()
        val total = state.selectedServices.sumOf { it.basePrice }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Tổng dự kiến", fontWeight = FontWeight.Bold)
            Text("$total đ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        if (state.submitError != null) {
            Text(state.submitError, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = { viewModel.setStep(3) }, modifier = Modifier.weight(1f), enabled = !state.isSubmitting) {
                Text("Sửa")
            }
            Button(
                onClick = { viewModel.submitBooking(token) },
                modifier = Modifier.weight(1f),
                enabled = !state.isSubmitting
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Hoàn tất Đặt lịch")
                }
            }
        }
    }
}
