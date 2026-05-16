package vn.vibe.booking.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AuthRepository {

    suspend fun login(phone: String, password: String): Result<LoginResult> = withContext(Dispatchers.IO) {
        runCatching {
            val payload = JSONObject()
                .put("phone", phone)
                .put("plainPassword", password)

            val responseText = postJson("https://be.aeoc.io.vn/api/user/authenticate", payload.toString())
            val responseJson = JSONObject(responseText)

            val code = responseJson.optInt("code", -1)
            if (code != 200) {
                throw IllegalStateException(responseJson.optString("message", "Đăng nhập thất bại"))
            }

            val result = responseJson.getJSONObject("result")
            LoginResult(
                token = result.optString("token"),
                userId = result.optLong("userId")
            )
        }
    }

    suspend fun register(name: String, phone: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val payload = JSONObject()
                .put("name", name)
                .put("phone", phone)
                .put("plainPassword", password)

            val responseText = postJson("https://be.aeoc.io.vn/api/user/create", payload.toString())
            val responseJson = JSONObject(responseText)

            val code = responseJson.optInt("code", -1)
            if (code != 200) {
                throw IllegalStateException(responseJson.optString("message", "Đăng ký thất bại"))
            }

            responseJson.optJSONObject("result")
                ?.optString("message", "Đăng ký thành công")
                ?: "Đăng ký thành công"
        }
    }

    private fun postJson(url: String, body: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            doOutput = true
        }

        return try {
            OutputStreamWriter(connection.outputStream).use { it.write(body) }
            val inputStream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            BufferedReader(inputStream.reader()).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}

data class LoginResult(
    val token: String,
    val userId: Long
)
