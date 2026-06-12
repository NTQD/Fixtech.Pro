package vn.vibe.booking.core.data

import kotlinx.coroutines.flow.Flow

interface TokenManager {
    val token: Flow<String?>
    suspend fun saveToken(token: String)
    suspend fun clearToken()
    suspend fun getTokenSync(): String?
}
