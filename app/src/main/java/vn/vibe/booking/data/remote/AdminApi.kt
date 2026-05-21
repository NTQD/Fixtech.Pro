package vn.vibe.booking.data.remote

import okhttp3.OkHttpClient
import org.json.JSONObject

class AdminApi(client: OkHttpClient) : BaseApiService(client) {
    suspend fun getUsers(keyword: String? = null, role: String? = null, active: Boolean? = null, page: Int = 1, limit: Int = 20, token: String): String {
        val query = buildString {
            append("?page=").append(page)
            append("&limit=").append(limit)
            if (!keyword.isNullOrBlank()) append("&keyword=").append(keyword)
            if (!role.isNullOrBlank()) append("&role=").append(role)
            if (active != null) append("&active=").append(active)
        }
        return get("/admin/users$query", auth(token))
    }

    suspend fun updateUser(id: Long, name: String?, phone: String?, email: String?, role: String?, active: Boolean?, token: String): String {
        val body = JSONObject()
            .put("name", name ?: "")
            .put("phone", phone ?: "")
            .put("email", email ?: "")
            .put("role", role ?: "")
            .put("active", active)
        return put("/admin/users/$id", body, auth(token))
    }

    suspend fun setUserActive(id: Long, active: Boolean, token: String): String {
        val body = JSONObject().put("active", active)
        return post("/admin/users/$id/active", body, auth(token))
    }

    private fun auth(token: String) = mapOf("Authorization" to "Bearer $token")
}
