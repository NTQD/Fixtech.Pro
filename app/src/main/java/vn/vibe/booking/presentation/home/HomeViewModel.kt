package vn.vibe.booking.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.vibe.booking.data.remote.AdminUserDto
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.ReviewDto
import vn.vibe.booking.data.remote.ServiceCategoryDto
import vn.vibe.booking.data.repository.HomeRepository
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.domain.model.UserInfo
import vn.vibe.booking.domain.repository.TokenRepository
import vn.vibe.booking.domain.repository.UserRepository

data class PageState<T>(
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val loading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<UserInfo>>(UiState.Idle)
    val userState: StateFlow<UiState<UserInfo>> = _userState.asStateFlow()

    private val _servicesState = MutableStateFlow(PageState<RepairServiceDto>())
    val servicesState: StateFlow<PageState<RepairServiceDto>> = _servicesState.asStateFlow()

    private val _bookingsState = MutableStateFlow(PageState<BookingSummaryDto>())
    val bookingsState: StateFlow<PageState<BookingSummaryDto>> = _bookingsState.asStateFlow()

    private val _reviewsState = MutableStateFlow<UiState<List<ReviewDto>>>(UiState.Idle)
    val reviewsState: StateFlow<UiState<List<ReviewDto>>> = _reviewsState.asStateFlow()

    private val _categoriesState = MutableStateFlow(PageState<ServiceCategoryDto>())
    val categoriesState: StateFlow<PageState<ServiceCategoryDto>> = _categoriesState.asStateFlow()

    private val _usersState = MutableStateFlow(PageState<AdminUserDto>())
    val usersState: StateFlow<PageState<AdminUserDto>> = _usersState.asStateFlow()

    fun loadUserInfo(token: String?) {
        if (token.isNullOrBlank()) return
        _userState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getUserInfo(token)
                .onSuccess { _userState.value = UiState.Success(it) }
                .onFailure { _userState.value = UiState.Error(it.message ?: "Không tải được thông tin người dùng") }
        }
    }

    fun loadServices(token: String?, keyword: String? = null, categoryId: Long? = null, page: Int = 1, limit: Int = 20) {
        if (token.isNullOrBlank()) return
        _servicesState.value = _servicesState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val result = homeRepository.getServices(token, keyword, categoryId, page, limit)) {
                is UiState.Success -> _servicesState.value = PageState(items = result.data, page = page, limit = limit, total = result.data.size, loading = false)
                is UiState.Empty -> _servicesState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                is UiState.Error -> _servicesState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                else -> _servicesState.value = PageState(page = page, limit = limit, loading = false)
            }
        }
    }

    fun loadBookings(token: String?, keyword: String? = null, status: String? = null, customerId: Int? = null, technicianId: Int? = null, page: Int = 1, limit: Int = 20) {
        if (token.isNullOrBlank()) return
        _bookingsState.value = _bookingsState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val result = homeRepository.getAdminBookings(token, keyword, status, customerId, technicianId, page, limit)) {
                is UiState.Success -> _bookingsState.value = PageState(items = result.data, page = page, limit = limit, total = result.data.size, loading = false)
                is UiState.Empty -> _bookingsState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                is UiState.Error -> _bookingsState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                else -> _bookingsState.value = PageState(page = page, limit = limit, loading = false)
            }
        }
    }

    fun loadServiceReviews(serviceId: Long) {
        _reviewsState.value = UiState.Loading
        viewModelScope.launch { _reviewsState.value = homeRepository.getServiceReviews(serviceId) }
    }

    fun loadCategories(token: String?, keyword: String? = null, page: Int = 1, limit: Int = 20) {
        if (token.isNullOrBlank()) return
        _categoriesState.value = _categoriesState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val result = homeRepository.getCategories(token, keyword, page, limit)) {
                is UiState.Success -> _categoriesState.value = PageState(items = result.data, page = page, limit = limit, total = result.data.size, loading = false)
                is UiState.Empty -> _categoriesState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                is UiState.Error -> _categoriesState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                else -> _categoriesState.value = PageState(page = page, limit = limit, loading = false)
            }
        }
    }

    fun loadUsers(token: String?, keyword: String? = null, role: String? = null, active: Boolean? = null, page: Int = 1, limit: Int = 20) {
        if (token.isNullOrBlank()) return
        _usersState.value = _usersState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val result = homeRepository.getUsers(token, keyword, role, active, page, limit)) {
                is UiState.Success -> _usersState.value = PageState(items = result.data, page = page, limit = limit, total = result.data.size, loading = false)
                is UiState.Empty -> _usersState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                is UiState.Error -> _usersState.value = PageState(page = page, limit = limit, total = 0, loading = false, error = result.message)
                else -> _usersState.value = PageState(page = page, limit = limit, loading = false)
            }
        }
    }

    fun updateUser(token: String, id: Long, name: String?, phone: String?, email: String?, role: String?, active: Boolean?, onDone: () -> Unit) = viewModelScope.launch {
        homeRepository.updateUser(token, id, name, phone, email, role, active)
        onDone()
    }

    fun setUserActive(token: String, id: Long, active: Boolean, onDone: () -> Unit) = viewModelScope.launch {
        homeRepository.setUserActive(token, id, active)
        onDone()
    }

    fun createCategory(token: String, name: String, description: String?, active: Boolean, onDone: () -> Unit) = viewModelScope.launch { homeRepository.createCategory(token, name, description, active); onDone() }
    fun updateCategory(token: String, id: Long, name: String, description: String?, active: Boolean?, onDone: () -> Unit) = viewModelScope.launch { homeRepository.updateCategory(token, id, name, description, active); onDone() }
    fun deleteCategory(token: String, id: Long, onDone: () -> Unit) = viewModelScope.launch { homeRepository.deleteCategory(token, id); onDone() }
    fun createService(token: String, categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean, onDone: () -> Unit) = viewModelScope.launch { homeRepository.createService(token, categoryId, name, shortDescription, description, basePrice, estimatedMinutes, warrantyDays, active); onDone() }
    fun updateService(token: String, id: Long, categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean, onDone: () -> Unit) = viewModelScope.launch { homeRepository.updateService(token, id, categoryId, name, shortDescription, description, basePrice, estimatedMinutes, warrantyDays, active); onDone() }
    fun deleteService(token: String, id: Long, onDone: () -> Unit) = viewModelScope.launch { homeRepository.deleteService(token, id); onDone() }
    fun confirmBooking(token: String, id: Long, note: String, onDone: () -> Unit) = viewModelScope.launch { homeRepository.confirmBooking(token, id, note); onDone() }
    fun assignTechnician(token: String, id: Long, technicianId: Long, note: String, onDone: () -> Unit) = viewModelScope.launch { homeRepository.assignTechnician(token, id, technicianId, note); onDone() }
    fun updateBookingStatus(token: String, id: Long, status: String, note: String, onDone: () -> Unit) = viewModelScope.launch { homeRepository.updateBookingStatus(token, id, status, note); onDone() }

    fun logout(onLoggedOut: () -> Unit) { viewModelScope.launch { tokenRepository.clearToken(); onLoggedOut() } }

    class Factory(
        private val userRepository: UserRepository,
        private val tokenRepository: TokenRepository,
        private val homeRepository: HomeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = HomeViewModel(userRepository, tokenRepository, homeRepository) as T
    }
}
