package vn.vibe.booking.domain.repository

import vn.vibe.booking.domain.model.AuthResult
import vn.vibe.booking.domain.model.UserInfo

interface AuthRepository {
    suspend fun login(phone: String, password: String): Result<AuthResult>
    suspend fun register(name: String, phone: String, password: String): Result<String>
    suspend fun getUserInfo(token: String): Result<UserInfo>
}
