package vn.vibe.booking.domain.model

data class AuthResult(
    val token: String,
    val userId: Long
)