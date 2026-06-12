package vn.vibe.booking.data.repository

import org.json.JSONException
import org.json.JSONObject
import vn.vibe.booking.data.remote.ApiException
import vn.vibe.booking.data.remote.AuthApi
import vn.vibe.booking.domain.model.UserInfo
import vn.vibe.booking.domain.repository.UserRepository
import java.io.IOException

import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: AuthApi
) : UserRepository {

    override suspend fun getUserInfo(token: String): Result<UserInfo> = runCatching {
        val responseJson = parseResponse(api.fetchUserInfo(token))
        ensureSuccess(responseJson)

        val result = responseJson.getJSONObject("result")
        UserInfo(
            id = result.optLong("id"),
            role = result.optString("role"),
            name = result.optString("name"),
            avatar = result.optString("avatar").takeIf { it.isNotBlank() },
            phone = result.optString("phone").takeIf { it.isNotBlank() },
            email = result.optString("email").takeIf { it.isNotBlank() }
        )
    }

    override suspend fun updateUserInfo(id: Long, name: String, email: String, phone: String, role: String, token: String, password: String?): Result<Boolean> = runCatching {
        val responseJson = parseResponse(api.updateProfile(id, name, email, phone, role, token, password))
        ensureSuccess(responseJson)
        true
    }

    private fun parseResponse(raw: String): JSONObject {
        return try {
            JSONObject(raw)
        } catch (e: JSONException) {
            throw IOException("Invalid server response", e)
        }
    }

    private fun ensureSuccess(responseJson: JSONObject) {
        val code = responseJson.optInt("code", -1)
        val message = responseJson.optString("message").ifBlank { "Unknown server error" }
        if (code != 200) throw ApiException(code, message)
    }
}
