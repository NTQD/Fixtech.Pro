package vn.vibe.booking.data.repository

import org.json.JSONException
import org.json.JSONObject
import vn.vibe.booking.data.remote.ApiClient
import vn.vibe.booking.data.remote.AuthApi
import vn.vibe.booking.domain.model.AuthResult
import vn.vibe.booking.domain.model.UserInfo
import vn.vibe.booking.domain.repository.AuthRepository
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApi
) : AuthRepository {

    override suspend fun login(phone: String, password: String): Result<AuthResult> = runCatching {
        val responseJson = ApiClient.parseObject(api.login(phone, password))
        ApiClient.requireSuccess(responseJson)

        val result = responseJson.optJSONObject("data")
            ?: responseJson.optJSONObject("result")
            ?: responseJson

        val token = result.optString("accessToken").ifBlank { result.optString("token") }
        val userId = result.optLong("userId", 0L)

        AuthResult(
            token = token,
            userId = userId
        )
    }

    override suspend fun register(name: String, phone: String, password: String): Result<String> = runCatching {
        val responseJson = ApiClient.parseObject(api.register(name, phone, password))
        ApiClient.requireSuccess(responseJson)
        responseJson.optString("message").ifBlank { "Đăng ký thành công" }
    }

    override suspend fun getUserInfo(token: String): Result<UserInfo> = runCatching {
        val responseJson = parseResponse(api.fetchUserInfo(token))
        ApiClient.requireSuccess(responseJson)

        val result = responseJson.optJSONObject("data")
            ?: responseJson.optJSONObject("result")
            ?: responseJson

        UserInfo(
            id = result.optLong("id"),
            role = result.optString("role").ifBlank { result.optString("role") },
            name = result.optString("name"),
            avatar = result.optString("avatar").takeIf { it.isNotBlank() },
            phone = result.optString("phone").takeIf { it.isNotBlank() },
            email = result.optString("email").takeIf { it.isNotBlank() }
        )
    }

    private fun parseResponse(raw: String): JSONObject {
        return try {
            JSONObject(raw)
        } catch (e: JSONException) {
            throw IOException("Invalid server response", e)
        }
    }
}
