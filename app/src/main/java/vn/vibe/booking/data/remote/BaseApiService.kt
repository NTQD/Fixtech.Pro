package vn.vibe.booking.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

abstract class BaseApiService(
    protected val client: OkHttpClient,
    protected val baseUrl: String = BackendConfig.BASE_URL
) {
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    protected suspend fun get(path: String, headers: Map<String, String> = emptyMap()): String = withContext(Dispatchers.IO) {
        execute(
            Request.Builder()
                .url("$baseUrl$path")
                .get()
                .apply { headers.forEach { (k, v) -> addHeader(k, v) } }
                .addHeader("Accept", "application/json")
                .build()
        )
    }

    protected suspend fun post(path: String, body: JSONObject, headers: Map<String, String> = emptyMap()): String = withContext(Dispatchers.IO) {
        execute(
            Request.Builder()
                .url("$baseUrl$path")
                .post(body.toString().toRequestBody(jsonMediaType))
                .apply { headers.forEach { (k, v) -> addHeader(k, v) } }
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
        )
    }

    protected suspend fun put(path: String, body: JSONObject, headers: Map<String, String> = emptyMap()): String = withContext(Dispatchers.IO) {
        execute(
            Request.Builder()
                .url("$baseUrl$path")
                .put(body.toString().toRequestBody(jsonMediaType))
                .apply { headers.forEach { (k, v) -> addHeader(k, v) } }
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build()
        )
    }

    protected suspend fun delete(path: String, headers: Map<String, String> = emptyMap()): String = withContext(Dispatchers.IO) {
        execute(
            Request.Builder()
                .url("$baseUrl$path")
                .delete()
                .apply { headers.forEach { (k, v) -> addHeader(k, v) } }
                .addHeader("Accept", "application/json")
                .build()
        )
    }

    private fun execute(request: Request): String {
        client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                var errorMsg = body
                try {
                    val json = JSONObject(body)
                    if (json.has("message")) errorMsg = json.getString("message")
                    else if (json.has("error")) errorMsg = json.getString("error")
                } catch (e: Exception) {}
                throw IOException(errorMsg.ifBlank { "Lỗi kết nối máy chủ" })
            }
            return body
        }
    }
}
