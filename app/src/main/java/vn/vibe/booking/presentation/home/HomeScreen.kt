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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
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

enum class BottomTab(val labelResId: Int, val icon: ImageVector) {
    Home(vn.vibe.booking.R.string.home_tab, Icons.Default.Home),
    Services(vn.vibe.booking.R.string.services_tab, Icons.AutoMirrored.Filled.List),
    Bookings(vn.vibe.booking.R.string.bookings_tab, Icons.Default.Menu),
    Profile(vn.vibe.booking.R.string.profile_tab, Icons.Default.Person)
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
        viewModel.loadCategories(token)
    }

    LaunchedEffect(token, isAdmin) {
        if (isAdmin) {
            viewModel.loadBookings(token)
            viewModel.loadUsers(token)
        } else {
            viewModel.loadMyBookings(token)
        }
    }

    val isLoading = userState is UiState.Loading
    val error = (userState as? UiState.Error)?.message

    var showThemeMenu by remember { mutableStateOf(false) }
    var showLanguageMenu by remember { mutableStateOf(false) }

    var showBookingForm by remember { mutableStateOf(false) }
    var initialBookingService by remember {
        mutableStateOf<vn.vibe.booking.data.remote.RepairServiceDto?>(
            null
        )
    }
    var initialBookingFilter by remember { mutableStateOf<String?>(null) }
    var showAdminBookings by remember { mutableStateOf(false) }
    var showAdminUsers by remember { mutableStateOf(false) }
    var showAdminRevenue by remember { mutableStateOf(false) }
    var showAdminInventory by remember { mutableStateOf(false) }
    var showTechBookings by remember { mutableStateOf(false) }

    if (!showBookingForm) {
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
                                    text = {
                                        Text(
                                            androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.theme_settings),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            androidx.compose.material.icons.Icons.Default.Settings,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showThemeMenu = true
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.language_settings),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Language,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showLanguageMenu = true
                                    }
                                )
                            }

                            if (showThemeMenu) {
                                AlertDialog(
                                    onDismissRequest = { showThemeMenu = false },
                                    title = { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.theme_settings)) },
                                    text = {
                                        Column {
                                            TextButton(onClick = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM); showThemeMenu = false; menuExpanded = false }) { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.theme_system)) }
                                            TextButton(onClick = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); showThemeMenu = false; menuExpanded = false }) { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.theme_light)) }
                                            TextButton(onClick = { AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); showThemeMenu = false; menuExpanded = false }) { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.theme_dark)) }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showThemeMenu = false }) { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.close)) }
                                    }
                                )
                            }
                            if (showLanguageMenu) {
                                AlertDialog(
                                    onDismissRequest = { showLanguageMenu = false },
                                    title = { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.language_settings)) },
                                    text = {
                                        Column {
                                            TextButton(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("vi")); showLanguageMenu = false; menuExpanded = false }) { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.lang_vi)) }
                                            TextButton(onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en")); showLanguageMenu = false; menuExpanded = false }) { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.lang_en)) }
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showLanguageMenu = false }) { Text(androidx.compose.ui.res.stringResource(id = vn.vibe.booking.R.string.close)) }
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
                            icon = { Icon(tab.icon, contentDescription = androidx.compose.ui.res.stringResource(id = tab.labelResId)) },
                            label = { Text(androidx.compose.ui.res.stringResource(id = tab.labelResId)) },
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                showBookingForm = false
                                showAdminBookings = false
                                showAdminUsers = false
                                showAdminRevenue = false
                                showAdminInventory = false
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
                                onManageUsers = { showAdminUsers = true },
                                onManageRevenue = { showAdminRevenue = true },
                                onManageInventory = { showAdminInventory = true }
                            )
                        } else if (isTechnician) {
                            vn.vibe.booking.presentation.technician.TechDashboardScreen(
                                viewModel = viewModel,
                                token = token,
                                onViewAssigned = { showTechBookings = true }
                            )
                        } else {
                            val servicesState by viewModel.servicesState.collectAsStateWithLifecycle()
                            val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
                            DashboardScreen(
                                userInfo = userInfo,
                                isLoading = isLoading,
                                error = error,
                                services = servicesState.items,
                                bookings = bookingsState.items,
                                onQuickBookClick = { showBookingForm = true },
                                onViewServicesClick = {
                                    selectedTab = tabs.indexOf(BottomTab.Services)
                                },
                                onServiceClick = { service ->
                                    initialBookingService = service
                                    showBookingForm = true
                                },
                                onViewInProgressClick = {
                                    initialBookingFilter = "IN_PROGRESS"
                                    selectedTab = tabs.indexOf(BottomTab.Bookings)
                                },
                                onViewCompletedClick = {
                                    initialBookingFilter = "COMPLETED"
                                    selectedTab = tabs.indexOf(BottomTab.Bookings)
                                }
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
                        if (isAdmin) {
                            vn.vibe.booking.presentation.admin.AdminBookingManagementScreen(
                                viewModel = viewModel,
                                token = token,
                                showTopBar = false,
                                initialFilterStatus = initialBookingFilter
                            )
                        } else {
                            vn.vibe.booking.presentation.booking.MyBookingsScreen(
                                viewModel = viewModel,
                                token = token,
                                initialFilterStatus = initialBookingFilter
                            )
                        }
                    }

                    BottomTab.Profile -> {
                        ProfileScreen(
                            userInfo = userInfo,
                            onLogout = onLogout,
                            onViewBookingHistory = {
                                initialBookingFilter = "COMPLETED"
                                selectedTab = tabs.indexOf(BottomTab.Bookings)
                            },
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
                                            android.widget.Toast.makeText(
                                                context,
                                                "Cập nhật thành công",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        onError = { errorMsg ->
                                            android.widget.Toast.makeText(
                                                context,
                                                "Lỗi: $errorMsg",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                        )
                    }
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

                if (showAdminRevenue) {
                    vn.vibe.booking.presentation.admin.AdminRevenueScreen(
                        viewModel = viewModel,
                        token = token,
                        onBack = { showAdminRevenue = false }
                    )
                }

                if (showAdminInventory) {
                    vn.vibe.booking.presentation.admin.AdminInventoryScreen(
                        viewModel = viewModel,
                        token = token,
                        onBack = { showAdminInventory = false }
                    )
                }
            }
        }
    }

    if (showBookingForm) {
        val bookingFormViewModel: vn.vibe.booking.presentation.booking.BookingFormViewModel =
            androidx.hilt.navigation.compose.hiltViewModel()
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
}