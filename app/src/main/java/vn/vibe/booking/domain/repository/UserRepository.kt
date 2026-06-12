package vn.vibe.booking.domain.repository

import vn.vibe.booking.domain.model.UserInfo

interface UserRepository {
    suspend fun getUserInfo(token: String): Result<UserInfo>
    suspend fun updateUserInfo(id: Long, name: String, email: String, phone: String, role: String, token: String): Result<Boolean>
}
