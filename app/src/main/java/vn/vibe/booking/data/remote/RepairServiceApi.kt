package vn.vibe.booking.data.remote

import okhttp3.OkHttpClient
import org.json.JSONObject

class RepairServiceApi(client: OkHttpClient) : BaseApiService(client) {
    suspend fun getServices(keyword: String? = null, categoryId: Long? = null, page: Int = 1, limit: Int = 20): String {
        val query = buildString {
            append("?page=").append(page)
            append("&limit=").append(limit)
            if (!keyword.isNullOrBlank()) append("&keyword=").append(keyword)
            if (categoryId != null) append("&categoryId=").append(categoryId)
        }
        return get("/services$query")
    }

    suspend fun getServiceDetail(id: Long): String = get("/services/$id")

    suspend fun createService(categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean, token: String): String {
        val body = JSONObject()
            .put("categoryId", categoryId)
            .put("name", name)
            .put("shortDescription", shortDescription ?: "")
            .put("description", description ?: "")
            .put("basePrice", basePrice)
            .put("estimatedMinutes", estimatedMinutes)
            .put("warrantyDays", warrantyDays)
            .put("active", active)
        return post("/admin/services", body, auth(token))
    }

    suspend fun updateService(id: Long, categoryId: Long, name: String, shortDescription: String?, description: String?, basePrice: Long, estimatedMinutes: Int, warrantyDays: Int, active: Boolean, token: String): String {
        val body = JSONObject()
            .put("categoryId", categoryId)
            .put("name", name)
            .put("shortDescription", shortDescription ?: "")
            .put("description", description ?: "")
            .put("basePrice", basePrice)
            .put("estimatedMinutes", estimatedMinutes)
            .put("warrantyDays", warrantyDays)
            .put("active", active)
        return put("/admin/services/$id", body, auth(token))
    }

    suspend fun deleteService(id: Long, token: String): String = delete("/admin/services/$id", auth(token))

    private fun auth(token: String) = mapOf("Authorization" to "Bearer $token")
}
