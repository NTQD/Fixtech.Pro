package vn.vibe.booking.presentation.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.domain.model.UserInfo

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    token: String?,
    onLogout: () -> Unit,
    contentPadding: PaddingValues
) {
    val userState by viewModel.userState.collectAsStateWithLifecycle()
    val userInfo = (userState as? UiState.Success<UserInfo>)?.data
    val isAdmin = userInfo?.role.equals("ADMIN", ignoreCase = true)

    var selectedTab by remember { mutableIntStateOf(0) }
    var menuExpanded by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    val visibleTabs = remember(isAdmin) {
        HomeTab.entries.filter { it != HomeTab.Console || isAdmin }
    }
    val hasTabs = visibleTabs.isNotEmpty()

    LaunchedEffect(token) {
        viewModel.loadUserInfo(token)
        viewModel.loadServices(token)
        if (isAdmin) viewModel.loadBookings(token) else viewModel.loadMyBookings(token)
        viewModel.loadCategories(token)
        if (isAdmin) viewModel.loadUsers(token)
    }

    if (selectedTab >= visibleTabs.size) selectedTab = 0

    val isLoading = userState is UiState.Loading
    val error = (userState as? UiState.Error)?.message

    val screenBg = Brush.linearGradient(colors = listOf(Color(0xFF050816), Color(0xFF0B1220), Color(0xFF111827)))
    val accent = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4)))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .padding(contentPadding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HomeTopBar(
                userInfo = userInfo,
                menuExpanded = menuExpanded,
                onMenuClick = { menuExpanded = true },
                onDismissMenu = { menuExpanded = false },
                onLogout = onLogout
            )

            if (hasTabs) {
                NavigationTabs(selectedTab = selectedTab, tabs = visibleTabs, onTabSelected = { selectedTab = it })
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (visibleTabs[selectedTab]) {
                    HomeTab.Home -> HomeDashboard(
                        userInfo = userInfo,
                        isLoading = isLoading,
                        error = error,
                        accent = accent,
                        onQuickBookClick = { selectedTab = visibleTabs.indexOf(HomeTab.Services).takeIf { it >= 0 } ?: selectedTab }
                    )
                    HomeTab.Services -> ServiceBookingScreen(viewModel = viewModel, token = token, contentPadding = PaddingValues(0.dp))
                    HomeTab.Bookings -> BookingTimelineScreen(viewModel = viewModel, token = token)
                    HomeTab.Console -> AdminConsoleScreen(viewModel = viewModel, token = token, contentPadding = PaddingValues(0.dp))
                    HomeTab.Profile -> ProfileScreen(userInfo = userInfo, accent = accent, onLogout = onLogout)
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    userInfo: UserInfo?,
    menuExpanded: Boolean,
    onMenuClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(avatarUrl = userInfo?.avatar, accent = Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF06B6D4))), size = 38.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Repair Booking", color = Color(0xFF94A3B8), fontSize = 10.sp)
                Text(text = userInfo?.name ?: "Chào mừng bạn", color = Color.White, style = MaterialTheme.typography.titleSmall, maxLines = 1)
            }
            IconButton(onClick = onMenuClick) { Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White) }
        }
        DropdownMenu(expanded = menuExpanded, onDismissRequest = onDismissMenu, modifier = Modifier.width(150.dp)) {
            DropdownMenuItem(text = { Text("Đăng xuất", fontSize = 12.sp) }, leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) }, onClick = { onDismissMenu(); onLogout() })
        }
    }
}

@Composable
private fun NavigationTabs(selectedTab: Int, tabs: List<HomeTab>, onTabSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        tabs.forEachIndexed { index, tab ->
            FilterChip(selected = selectedTab == index, onClick = { onTabSelected(index) }, label = { tab.label?.let { Text(it) } }, leadingIcon = { Icon(tab.icon, contentDescription = null) })
        }
    }
}
