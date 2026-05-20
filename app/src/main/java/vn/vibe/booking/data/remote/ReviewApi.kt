package vn.vibe.booking.data.remote

import okhttp3.OkHttpClient
import org.json.JSONObject

class ReviewApi(client: OkHttpClient) : BaseApiService(client) {
    suspend fun createReview(bookingId: Long, rating: Int, comment: String, token: String): String {
        val body = JSONObject()
            .put("rating", rating)
            .put("comment", comment)
        return post("/bookings/$bookingId/review", body, auth(token))
    }

    suspend fun getServiceReviews(serviceId: Long, page: Int = 1, limit: Int = 20): String {
        val query = "?page=$page&limit=$limit"
        return get("/services/$serviceId/reviews$query")
    }

    private fun auth(token: String) = mapOf("Authorization" to "Bearer $token")
}
