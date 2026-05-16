package vn.vibe.booking.domain.usecase

import vn.vibe.booking.domain.model.AuthResult
import vn.vibe.booking.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phone: String, password: String): Result<AuthResult> {
        return authRepository.login(phone, password)
    }
}