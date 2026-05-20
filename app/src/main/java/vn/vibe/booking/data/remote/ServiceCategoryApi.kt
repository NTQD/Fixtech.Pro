package vn.vibe.booking.data.remote

import okhttp3.OkHttpClient
import org.json.JSONObject

class ServiceCategoryApi(client: OkHttpClient) : BaseApiService(client) {
    suspend fun getAdminCategories(keyword: String? = null, page: Int = 1, limit: Int = 20, token: String): String {
        val query = buildString {
            append("?page=").append(page)
            append("&limit=").append(limit)
            if (!keyword.isNullOrBlank()) append("&keyword=").append(keyword)
        }
        return get("/admin/service-categories$query", auth(token))
    }

    suspend fun createCategory(name: String, description: String?, active: Boolean, token: String): String {
        val body = JSONObject()
            .put("name", name)
            .put("description", description ?: "")
            .put("active", active)
        return post("/admin/service-categories", body, auth(token))
    }

    suspend fun updateCategory(id: Long, name: String, description: String?, active: Boolean, token: String): String {
        val body = JSONObject()
            .put("name", name)
            .put("description", description ?: "")
            .put("active", active)
        return put("/admin/service-categories/$id", body, auth(token))
    }

    suspend fun deleteCategory(id: Long, token: String): String {
        return delete("/admin/service-categories/$id", auth(token))
    }

    private fun auth(token: String) = mapOf("Authorization" to "Bearer $token")
}
