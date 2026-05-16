package vn.vibe.booking.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableLiveData(AuthUiState())
    val loginState: LiveData<AuthUiState> = _loginState

    private val _registerState = MutableLiveData(AuthUiState())
    val registerState: LiveData<AuthUiState> = _registerState

    fun login(phone: String, password: String, onSuccess: (LoginResult) -> Unit) {
        _loginState.value = AuthUiState(loading = true)
        viewModelScope.launch {
            repository.login(phone, password)
                .onSuccess {
                    _loginState.value = AuthUiState(successMessage = "Đăng nhập thành công")
                    onSuccess(it)
                }
                .onFailure {
                    _loginState.value = AuthUiState(errorMessage = it.message ?: "Đăng nhập thất bại")
                }
        }
    }

    fun register(name: String, phone: String, password: String, onSuccess: () -> Unit) {
        _registerState.value = AuthUiState(loading = true)
        viewModelScope.launch {
            repository.register(name, phone, password)
                .onSuccess {
                    _registerState.value = AuthUiState(successMessage = it)
                    onSuccess()
                }
                .onFailure {
                    _registerState.value = AuthUiState(errorMessage = it.message ?: "Đăng ký thất bại")
                }
        }
    }
}
