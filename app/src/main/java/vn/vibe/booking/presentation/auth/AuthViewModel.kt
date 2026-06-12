package vn.vibe.booking.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.vibe.booking.core.data.TokenManager
import vn.vibe.booking.data.remote.ApiException
import vn.vibe.booking.domain.usecase.LoginUseCase
import vn.vibe.booking.domain.usecase.RegisterUseCase
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(AuthUiState())
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(AuthUiState())
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    fun login(phone: String, password: String, onSuccess: () -> Unit) {
        _loginState.update { AuthUiState(isLoading = true) }
        viewModelScope.launch {
            loginUseCase(phone, password)
                .onSuccess { result ->
                    tokenManager.saveToken(result.token)
                    _loginState.update { AuthUiState(isLoading = false, message = "success") }
                    onSuccess()
                }
                .onFailure { throwable ->
                    val errorMessage = when (throwable) {
                        is ApiException -> throwable.message
                        else -> throwable.message ?: "Đăng nhập thất bại"
                    }
                    _loginState.update { AuthUiState(isLoading = false, error = errorMessage) }
                }
        }
    }

    fun register(name: String, phone: String, password: String, onSuccess: () -> Unit) {
        _registerState.update { AuthUiState(isLoading = true) }
        viewModelScope.launch {
            registerUseCase(name, phone, password)
                .onSuccess { successMessage ->
                    _registerState.update { AuthUiState(isLoading = false, message = successMessage) }
                    onSuccess()
                }
                .onFailure { throwable ->
                    val errorMessage = when (throwable) {
                        is ApiException -> throwable.message
                        else -> throwable.message ?: "Đăng ký thất bại"
                    }
                    _registerState.update { AuthUiState(isLoading = false, error = errorMessage) }
                }
        }
    }

    fun clearLoginTransientState() {
        _loginState.update { it.copy(message = null, error = null) }
    }

    fun clearRegisterTransientState() {
        _registerState.update { it.copy(message = null, error = null) }
    }
}
