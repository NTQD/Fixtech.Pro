package vn.vibe.booking.data.remote

import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject

class BookingApi(client: OkHttpClient) : BaseApiService(client) {
    suspend fun createBooking(
        customerName: String,
        customerPhone: String,
        customerEmail: String?,
        deviceType: String,
        deviceBrand: String,
        deviceModel: String,
        issueDescription: String,
        preferredDate: String,
        preferredTimeSlot: String,
        address: String,
        note: String?,
        items: List<BookingItemRequest>,
        token: String
    ): String {
        val body = JSONObject()
            .put("customerName", customerName)
            .put("customerPhone", customerPhone)
            .put("customerEmail", customerEmail ?: "")
            .put("deviceType", deviceType)
            .put("deviceBrand", deviceBrand)
            .put("deviceModel", deviceModel)
            .put("issueDescription", issueDescription)
            .put("preferredDate", preferredDate)
            .put("preferredTimeSlot", preferredTimeSlot)
            .put("address", address)
            .put("note", note ?: "")
            .put("items", JSONArray(items.map { it.toJson() }))
        return post("/bookings", body, auth(token))
    }

    suspend fun getMyBookings(status: String? = null, page: Int = 1, limit: Int = 20, token: String): String {
        val query = buildString {
            append("?page=").append(page)
            append("&limit=").append(limit)
            if (!status.isNullOrBlank()) append("&status=").append(status)
        }
        return get("/bookings/me$query", auth(token))
    }

    suspend fun getAdminBookings(keyword: String? = null, status: String? = null, customerId: Int? = null, technicianId: Int? = null, page: Int = 1, limit: Int = 20, token: String): String {
        val query = buildString {
            append("?page=").append(page)
            append("&limit=").append(limit)
            if (!keyword.isNullOrBlank()) append("&keyword=").append(keyword)
            if (!status.isNullOrBlank()) append("&status=").append(status)
            if (customerId != null) append("&customerId=").append(customerId)
            if (technicianId != null) append("&technicianId=").append(technicianId)
        }
        return get("/admin/bookings$query", auth(token))
    }

    suspend fun getBookingDetail(id: Long, token: String): String = get("/bookings/$id", auth(token))

    suspend fun cancelBooking(id: Long, reason: String, token: String): String {
        val body = JSONObject().put("reason", reason)
        return post("/bookings/$id/cancel", body, auth(token))
    }

    suspend fun confirmBooking(id: Long, note: String, token: String): String {
        val body = JSONObject().put("note", note)
        return post("/admin/bookings/$id/confirm", body, auth(token))
    }

    suspend fun assignTechnician(id: Long, technicianId: Long, note: String, token: String): String {
        val body = JSONObject().put("technicianId", technicianId).put("note", note)
        return post("/admin/bookings/$id/assign-technician", body, auth(token))
    }

    suspend fun updateStatus(id: Long, status: String, note: String, scheduledAt: String? = null, token: String): String {
        val body = JSONObject()
            .put("status", status)
            .put("note", note)
        if (!scheduledAt.isNullOrBlank()) body.put("scheduledAt", scheduledAt)
        return post("/technician/bookings/$id/status", body, auth(token))
    }

    suspend fun addNote(id: Long, note: String, token: String): String {
        val body = JSONObject().put("note", note)
        return post("/technician/bookings/$id/notes", body, auth(token))
    }

    private fun auth(token: String) = mapOf("Authorization" to "Bearer $token")
}

data class BookingItemRequest(
    val serviceId: Long,
    val quantity: Int
) {
    fun toJson() = JSONObject().put("serviceId", serviceId).put("quantity", quantity)
}
