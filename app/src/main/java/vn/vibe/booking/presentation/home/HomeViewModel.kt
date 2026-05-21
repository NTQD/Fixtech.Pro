package vn.vibe.booking.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.vibe.booking.data.remote.BookingSummaryDto
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.remote.ReviewDto
import vn.vibe.booking.data.remote.ServiceCategoryDto
import vn.vibe.booking.data.repository.HomeRepository
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.domain.model.UserInfo
import vn.vibe.booking.domain.repository.TokenRepository
import vn.vibe.booking.domain.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<UserInfo>>(UiState.Idle)
    val userState: StateFlow<UiState<UserInfo>> = _userState.asStateFlow()

    private val _servicesState = MutableStateFlow<UiState<List<RepairServiceDto>>>(UiState.Idle)
    val servicesState: StateFlow<UiState<List<RepairServiceDto>>> = _servicesState.asStateFlow()

    private val _bookingsState = MutableStateFlow<UiState<List<BookingSummaryDto>>>(UiState.Idle)
    val bookingsState: StateFlow<UiState<List<BookingSummaryDto>>> = _bookingsState.asStateFlow()

    private val _reviewsState = MutableStateFlow<UiState<List<ReviewDto>>>(UiState.Idle)
    val reviewsState: StateFlow<UiState<List<ReviewDto>>> = _reviewsState.asStateFlow()

    private val _categoriesState = MutableStateFlow<UiState<List<ServiceCategoryDto>>>(UiState.Idle)
    val categoriesState: StateFlow<UiState<List<ServiceCategoryDto>>> = _categoriesState.asStateFlow()

    fun loadUserInfo(token: String?) {
        if (token.isNullOrBlank()) return
        _userState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getUserInfo(token)
                .onSuccess { _userState.value = UiState.Success(it) }
                .onFailure { _userState.value = UiState.Error(it.message ?: "Không tải được thông tin người dùng") }
        }
    }

    fun loadServices(token: String?) {
        _servicesState.value = UiState.Loading
        viewModelScope.launch {
            val result = homeRepository.getServices(token)
            _servicesState.value = result
        }
    }

    fun loadBookings(token: String?) {
        if (token.isNullOrBlank()) return
        _bookingsState.value = UiState.Loading
        viewModelScope.launch {
            _bookingsState.value = homeRepository.getMyBookings(token)
        }
    }

    fun loadServiceReviews(serviceId: Long) {
        _reviewsState.value = UiState.Loading
        viewModelScope.launch {
            _reviewsState.value = homeRepository.getServiceReviews(serviceId)
        }
    }

    fun loadCategories(token: String?) {
        if (token.isNullOrBlank()) return
        _categoriesState.value = UiState.Loading
        viewModelScope.launch {
            _categoriesState.value = homeRepository.getCategories(token)
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            tokenRepository.clearToken()
            _userState.value = UiState.Idle
            _servicesState.value = UiState.Idle
            _bookingsState.value = UiState.Idle
            _reviewsState.value = UiState.Idle
            _categoriesState.value = UiState.Idle
            onLoggedOut()
        }
    }

    class Factory(
        private val userRepository: UserRepository,
        private val tokenRepository: TokenRepository,
        private val homeRepository: HomeRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(userRepository, tokenRepository, homeRepository) as T
        }
    }
}
