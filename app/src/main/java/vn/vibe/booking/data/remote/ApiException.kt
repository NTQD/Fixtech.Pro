package vn.vibe.booking.data.remote

class ApiException(
    val code: Int,
    override val message: String
) : RuntimeException(message)
