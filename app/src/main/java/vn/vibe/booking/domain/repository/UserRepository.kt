package vn.vibe.booking.domain.repository

import vn.vibe.booking.domain.model.UserInfo

interface UserRepository {
    suspend fun getUserInfo(token: String): Result<UserInfo>
}
