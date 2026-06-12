package vn.vibe.booking.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

import javax.inject.Inject

class AuthApi @Inject constructor(
    private val client: OkHttpClient
) {
    private val baseUrl: String = BackendConfig.BASE_URL
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
            .put("password", password)
            .put("plainPassword", password)
            .toString()
        postJson("$baseUrl/authenticate/register", payload)
    }

    suspend fun fetchUserInfo(token: String): String = withContext(Dispatchers.IO) {
        getJson("$baseUrl/authenticate/me", token)
    }

    suspend fun updateProfile(id: Long, name: String, email: String, phone: String, role: String, token: String): String = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("name", name)
            .put("email", email)
            .put("phone", phone)
            .put("role", role)
            .put("active", true) // Assuming the user is active if they are logged in
            .toString()
        putJson("$baseUrl/admin/users/$id", payload, token)
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

    private fun putJson(url: String, body: String, token: String): String {
        val request = Request.Builder()
            .url(url)
            .put(body.toRequestBody(jsonMediaType))
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return execute(request)
    }

    private fun execute(request: Request): String {
        println("[API-REQUEST] ${request.method} ${request.url}")
        println("[API-CURL] ${CurlDebug.request(request)}")

        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            println("[API-RESPONSE] ${response.code} ${response.message} -> $body")

            if (!response.isSuccessful) {
                throw ApiException(response.code, extractMessage(body, response.message))
            }
            return body
        }
    }

    private fun extractMessage(body: String, fallback: String): String {
        return runCatching {
            val json = JSONObject(body)
            json.optString("message").ifBlank { json.optString("error").ifBlank { fallback } }
        }.getOrDefault(fallback)
    }
}
