package vn.vibe.booking.domain.usecase

import vn.vibe.booking.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(name: String, phone: String, password: String): Result<String> {
        return authRepository.register(name, phone, password)
    }
}