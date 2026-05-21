package vn.vibe.booking.domain.model

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Empty(val message: String = "Không có dữ liệu") : UiState<Nothing>
    data class Error(val message: String, val code: Int? = null) : UiState<Nothing>
}
