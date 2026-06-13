package vn.vibe.booking.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.vibe.booking.data.remote.BookingItemRequest
import vn.vibe.booking.data.remote.RepairServiceDto
import vn.vibe.booking.data.repository.HomeRepository
import vn.vibe.booking.domain.model.UiState
import javax.inject.Inject

data class BookingFormState(
    val currentStep: Int = 0,
    val customerName: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val deviceType: String = "",
    val deviceBrand: String = "",
    val deviceModel: String = "",
    val issueDescription: String = "",
    val preferredDate: String = "",
    val preferredTimeSlot: String = "",
    val address: String = "",
    val note: String = "",
    val selectedServices: List<RepairServiceDto> = emptyList(),
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val submitSuccess: Boolean = false
)

@HiltViewModel
class BookingFormViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BookingFormState())
    val state: StateFlow<BookingFormState> = _state.asStateFlow()

    fun updateCustomerInfo(name: String, phone: String, email: String) {
        _state.update { it.copy(customerName = name, customerPhone = phone, customerEmail = email) }
    }

    fun updateDeviceInfo(type: String, brand: String, model: String, issue: String) {
        _state.update { it.copy(deviceType = type, deviceBrand = brand, deviceModel = model, issueDescription = issue) }
    }

    fun updateScheduleInfo(date: String, timeSlot: String, address: String, note: String) {
        _state.update { it.copy(preferredDate = date, preferredTimeSlot = timeSlot, address = address, note = note) }
    }

    fun addService(service: RepairServiceDto) {
        _state.update {
            val current = it.selectedServices.toMutableList()
            if (!current.any { s -> s.id == service.id }) {
                current.add(service)
            }
            it.copy(selectedServices = current)
        }
    }

    fun removeService(serviceId: Long) {
        _state.update { it.copy(selectedServices = it.selectedServices.filter { s -> s.id != serviceId }) }
    }

    fun setStep(step: Int) {
        _state.update { it.copy(currentStep = step) }
    }

    fun submitBooking(token: String?) {
        if (token.isNullOrBlank()) return
        val currentState = _state.value
        
        if (currentState.customerName.isBlank() || currentState.customerPhone.isBlank() ||
            currentState.deviceType.isBlank() || currentState.deviceBrand.isBlank() ||
            currentState.deviceModel.isBlank() || currentState.issueDescription.isBlank() ||
            currentState.preferredDate.isBlank() || currentState.preferredTimeSlot.isBlank() ||
            currentState.address.isBlank()) {
            _state.update { it.copy(submitError = "Vui lòng nhập đầy đủ thông tin bắt buộc (*)") }
            return
        }
        
        if (currentState.selectedServices.isEmpty()) {
            _state.update { it.copy(submitError = "Vui lòng chọn ít nhất 1 dịch vụ") }
            return
        }

        val timeParts = currentState.preferredTimeSlot.split(":")
        if (timeParts.size == 2) {
            val hour = timeParts[0].toIntOrNull() ?: 0
            if (hour < 8 || hour >= 22) {
                _state.update { it.copy(submitError = "Vui lòng chọn giờ hẹn trong khung giờ hành chính (08:00 - 22:00)") }
                return
            }
        }

        _state.update { it.copy(isSubmitting = true, submitError = null) }
        viewModelScope.launch {
            val items = currentState.selectedServices.map {
                BookingItemRequest(serviceId = it.id, quantity = 1)
            }
            
            try {
                when (val result = homeRepository.createBooking(
                    token = token,
                    customerName = currentState.customerName,
                    customerPhone = currentState.customerPhone,
                    customerEmail = currentState.customerEmail.ifBlank { null },
                    deviceType = currentState.deviceType,
                    deviceBrand = currentState.deviceBrand,
                    deviceModel = currentState.deviceModel,
                    issueDescription = currentState.issueDescription,
                    preferredDate = currentState.preferredDate,
                    preferredTimeSlot = currentState.preferredTimeSlot,
                    address = currentState.address,
                    note = currentState.note.ifBlank { null },
                    items = items
                )) {
                    is UiState.Success -> _state.update { it.copy(isSubmitting = false, submitSuccess = true) }
                    is UiState.Error -> _state.update { it.copy(isSubmitting = false, submitError = result.message) }
                    else -> _state.update { it.copy(isSubmitting = false, submitError = "Unknown error") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isSubmitting = false, submitError = e.message ?: "Không thể tạo booking") }
            }
        }
    }

    fun resetState() {
        _state.value = BookingFormState()
    }
}
