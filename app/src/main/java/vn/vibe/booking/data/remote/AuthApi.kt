package vn.vibe.booking.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class AuthApi(
    private val client: OkHttpClient,
    private val baseUrl: String = BackendConfig.BASE_URL
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun login(phone: String, password: String): String = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("phone", phone)
            .put("email", JSONObject.NULL)
            .put("password", password)
            .toString()
        postJson("$baseUrl/authenticate/login", payload)
    }

    suspend fun register(name: String, phone: String, password: String): String = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("name", name)
            .put("phone", phone)
            .put("email", "")
            .put("password", password)
            .put("plainPassword", password)
            .put("role", "USER")
            .toString()
        postJson("$baseUrl/authenticate/register", payload)
    }

    suspend fun fetchUserInfo(token: String): String = withContext(Dispatchers.IO) {
        getJson("$baseUrl/authenticate/me", token)
    }

    private fun postJson(url: String, body: String): String {
        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody(jsonMediaType))
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()

        return execute(request)
    }

    private fun getJson(url: String, token: String): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return execute(request)
    }

    private fun execute(request: Request): String {
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}: $body")
            }
            return body
        }
    }
}
