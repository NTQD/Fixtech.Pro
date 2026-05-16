package vn.vibe.booking.domain.repository

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    val token: Flow<String?>
    suspend fun saveToken(token: String)
    suspend fun clearToken()
    suspend fun getCurrentToken(): String?
}