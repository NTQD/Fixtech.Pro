package vn.vibe.booking.domain.model

data class UserInfo(
    val id: Long,
    val role: String,
    val name: String,
    val avatar: String?,
    val phone: String?,
    val email: String?
)
