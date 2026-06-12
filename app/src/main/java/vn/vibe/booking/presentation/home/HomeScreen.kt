package vn.vibe.booking.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.domain.model.UserInfo

enum class BottomTab(val label: String, val icon: ImageVector) {
    Home("Trang chủ", Icons.Default.Home),
    Services("Dịch vụ", Icons.AutoMirrored.Filled.List),
    Bookings("Lịch đặt", Icons.Default.Menu),
    Profile("Hồ sơ", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    token: String?,
    onLogout: () -> Unit,
    contentPadding: PaddingValues
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val userInfo = (userState as? UiState.Success<UserInfo>)?.data
    val isAdmin = userInfo?.role.equals("ADMIN", ignoreCase = true)
    val isTechnician = userInfo?.role.equals("TECHNICIAN", ignoreCase = true)

    var selectedTab by remember { mutableIntStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }

    val tabs = BottomTab.entries

    LaunchedEffect(token) {
        viewModel.loadUserInfo(token)
        viewModel.loadServices(token)
        if (isAdmin) viewModel.loadBookings(token) else viewModel.loadMyBookings(token)
        viewModel.loadCategories(token)
        if (isAdmin) viewModel.loadUsers(token)
    }

    val isLoading = userState is UiState.Loading
    val error = (userState as? UiState.Error)?.message

    var showBookingForm by remember { mutableStateOf(false) }
    var initialBookingService by remember { mutableStateOf<vn.vibe.booking.data.remote.RepairServiceDto?>(null) }
    var showAdminBookings by remember { mutableStateOf(false) }
    var showAdminUsers by remember { mutableStateOf(false) }
    var showTechBookings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Fixtech.Pro",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = userInfo?.name ?: "Xin chào",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.width(160.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Đăng xuất", style = MaterialTheme.typography.bodyMedium) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            showBookingForm = false
                            showAdminBookings = false
                            showAdminUsers = false
                            showTechBookings = false
                        }
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(MaterialTheme.colorScheme.background)
    ) { innerPadding ->
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (tabs[selectedTab]) {
                BottomTab.Home -> {
                    if (isAdmin) {
                        vn.vibe.booking.presentation.admin.AdminDashboardScreen(
                            viewModel = viewModel,
                            token = token,
                            onManageBookings = { showAdminBookings = true },
                            onManageUsers = { showAdminUsers = true }
                        )
                    } else if (isTechnician) {
                        vn.vibe.booking.presentation.technician.TechDashboardScreen(
                            viewModel = viewModel,
                            token = token,
                            onViewAssigned = { showTechBookings = true }
                        )
                    } else {
                        DashboardScreen(
                            userInfo = userInfo,
                            isLoading = isLoading,
                            error = error,
                            onQuickBookClick = { showBookingForm = true },
                            onViewServicesClick = { selectedTab = tabs.indexOf(BottomTab.Services) }
                        )
                    }
                }
                BottomTab.Services -> {
                    ServicesScreen(
                        viewModel = viewModel,
                        token = token,
                        onBookService = { service ->
                            initialBookingService = service
                            showBookingForm = true
                        }
                    )
                }
                BottomTab.Bookings -> {
                    vn.vibe.booking.presentation.booking.MyBookingsScreen(
                        viewModel = viewModel,
                        token = token
                    )
                }
                BottomTab.Profile -> {
                    ProfileScreen(
                        userInfo = userInfo,
                        onLogout = onLogout,
                        onUpdateProfile = { name, email, password ->
                            if (userInfo != null) {
                                viewModel.updateProfile(
                                    id = userInfo.id,
                                    name = name,
                                    email = email,
                                    phone = userInfo.phone ?: "",
                                    role = userInfo.role ?: "",
                                    token = token,
                                    password = password,
                                    onSuccess = {
                                        android.widget.Toast.makeText(context, "Cập nhật thành công", android.widget.Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { errorMsg ->
                                        android.widget.Toast.makeText(context, "Lỗi: $errorMsg", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
        
        if (showBookingForm) {
            val bookingFormViewModel: vn.vibe.booking.presentation.booking.BookingFormViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            vn.vibe.booking.presentation.booking.BookingFormScreen(
                formViewModel = bookingFormViewModel,
                homeViewModel = viewModel,
                token = token,
                initialService = initialBookingService,
                onBack = { showBookingForm = false; initialBookingService = null },
                onSuccess = { 
                    showBookingForm = false 
                    initialBookingService = null
                    selectedTab = tabs.indexOf(BottomTab.Bookings)
                    viewModel.loadMyBookings(token)
                }
            )
        }

        if (showAdminBookings) {
            vn.vibe.booking.presentation.admin.AdminBookingManagementScreen(
                viewModel = viewModel,
                token = token,
                onBack = { showAdminBookings = false }
            )
        }

        if (showAdminUsers) {
            vn.vibe.booking.presentation.admin.AdminUserManagementScreen(
                viewModel = viewModel,
                token = token,
                onBack = { showAdminUsers = false }
            )
        }

        if (showTechBookings) {
            vn.vibe.booking.presentation.technician.TechBookingListScreen(
                viewModel = viewModel,
                token = token,
                onBack = { showTechBookings = false }
            )
        }
    }
}
