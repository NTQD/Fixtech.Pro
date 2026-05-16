package vn.vibe.booking.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AuthApi(
    private val baseUrl: String = "https://be.aeoc.io.vn/api"
) {

    suspend fun login(phone: String, password: String): String = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("phone", phone)
            .put("plainPassword", password)
        postJson("$baseUrl/user/authenticate", payload.toString())
    }

    suspend fun register(name: String, phone: String, password: String): String = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("name", name)
            .put("phone", phone)
            .put("plainPassword", password)
        postJson("$baseUrl/user/create", payload.toString())
    }

    suspend fun fetchUserInfo(token: String): String = withContext(Dispatchers.IO) {
        getJson("$baseUrl/user/info", token)
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
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            BufferedReader(stream.reader()).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun getJson(url: String, token: String): String {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Authorization", "Bearer $token")
        }

        return try {
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            BufferedReader(stream.reader()).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
