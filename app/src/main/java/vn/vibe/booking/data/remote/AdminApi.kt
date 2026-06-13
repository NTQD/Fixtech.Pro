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

    suspend fun createUser(name: String, phone: String?, email: String?, role: String?, active: Boolean?, token: String): String {
        val body = JSONObject()
            .put("name", name)
            .put("phone", phone ?: "")
            .put("email", email ?: "")
            .put("role", role ?: "")
            .put("active", active)
        return post("/admin/users", body, auth(token))
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

    suspend fun deleteUser(id: Long, token: String): String = delete("/admin/users/$id", auth(token))

    suspend fun setUserActive(id: Long, active: Boolean, token: String): String {
        val body = JSONObject().put("active", active)
        return post("/admin/users/$id/active", body, auth(token))
    }

    suspend fun getCategories(keyword: String? = null, page: Int = 1, limit: Int = 20, token: String): String {
        val query = buildString {
            append("?page=").append(page)
            append("&limit=").append(limit)
            if (!keyword.isNullOrBlank()) append("&keyword=").append(keyword)
        }
        return get("/admin/service-categories$query", auth(token))
    }

    suspend fun createCategory(name: String, description: String?, active: Boolean, token: String): String {
        val body = JSONObject().put("name", name).put("description", description ?: "").put("active", active)
        return post("/admin/service-categories", body, auth(token))
    }

    suspend fun updateCategory(id: Long, name: String, description: String?, active: Boolean?, token: String): String {
        val body = JSONObject().put("name", name).put("description", description ?: "").put("active", active)
        return put("/admin/service-categories/$id", body, auth(token))
    }

    suspend fun deleteCategory(id: Long, token: String): String = delete("/admin/service-categories/$id", auth(token))

    suspend fun getDashboardOverview(token: String): String = get("/api/v1/admin/dashboard/overview", auth(token))
    
    suspend fun getRevenueDetails(page: Int = 1, limit: Int = 20, token: String): String = get("/api/v1/admin/dashboard/revenue-details?page=$page&limit=$limit", auth(token))

    suspend fun getInventoryItems(keyword: String? = null, page: Int = 1, limit: Int = 20, token: String): String {
        val query = buildString {
            append("?page=").append(page)
            append("&limit=").append(limit)
            if (!keyword.isNullOrBlank()) append("&keyword=").append(keyword)
        }
        return get("/api/v1/admin/inventory$query", auth(token))
    }

    suspend fun createInventoryItem(name: String, description: String?, price: Double, stock: Int, imageUrl: String?, token: String): String {
        val body = JSONObject()
            .put("name", name)
            .put("description", description ?: "")
            .put("price", price)
            .put("stock", stock)
            .put("imageUrl", imageUrl ?: "")
        return post("/api/v1/admin/inventory", body, auth(token))
    }

    suspend fun updateInventoryItem(id: Long, name: String, description: String?, price: Double, stock: Int, imageUrl: String?, token: String): String {
        val body = JSONObject()
            .put("name", name)
            .put("description", description ?: "")
            .put("price", price)
            .put("stock", stock)
            .put("imageUrl", imageUrl ?: "")
        return put("/api/v1/admin/inventory/$id", body, auth(token))
    }

    suspend fun deleteInventoryItem(id: Long, token: String): String = delete("/api/v1/admin/inventory/$id", auth(token))

    private fun auth(token: String) = mapOf("Authorization" to "Bearer $token")
}
