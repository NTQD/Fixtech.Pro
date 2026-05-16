package vn.vibe.booking.domain.usecase

import kotlinx.coroutines.flow.Flow
import vn.vibe.booking.domain.repository.TokenRepository

class ObserveAuthStateUseCase(
    private val tokenRepository: TokenRepository
) {
    operator fun invoke(): Flow<String?> = tokenRepository.token
}
