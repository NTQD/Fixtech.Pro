package vn.vibe.booking.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.vibe.booking.domain.model.UiState
import vn.vibe.booking.domain.model.UserInfo
import vn.vibe.booking.domain.repository.TokenRepository
import vn.vibe.booking.domain.repository.UserRepository

class HomeViewModel(
    private val userRepository: UserRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<UserInfo>>(UiState.Idle)
    val userState: StateFlow<UiState<UserInfo>> = _userState.asStateFlow()

    fun loadUserInfo(token: String?) {
        if (token.isNullOrBlank()) return
        _userState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getUserInfo(token)
                .onSuccess { _userState.value = UiState.Success(it) }
                .onFailure { _userState.value = UiState.Error(it.message ?: "Không tải được thông tin người dùng") }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            tokenRepository.clearToken()
            _userState.value = UiState.Idle
            onLoggedOut()
        }
    }

    class Factory(
        private val userRepository: UserRepository,
        private val tokenRepository: TokenRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(userRepository, tokenRepository) as T
        }
    }
}
