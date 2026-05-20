package vn.vibe.booking.data.repository

import org.json.JSONException
import org.json.JSONObject
import vn.vibe.booking.data.remote.ApiException
import vn.vibe.booking.data.remote.AuthApi
import vn.vibe.booking.domain.model.UserInfo
import vn.vibe.booking.domain.repository.UserRepository
import java.io.IOException

class UserRepositoryImpl(
    private val api: AuthApi
) : UserRepository {

    override suspend fun getUserInfo(token: String): Result<UserInfo> = runCatching {
        val responseJson = parseResponse(api.fetchUserInfo(token))
        ensureSuccess(responseJson)

        val result = responseJson.getJSONObject("result")
        UserInfo(
            id = result.optLong("id"),
            roleName = result.optString("roleName"),
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

    private fun ensureSuccess(responseJson: JSONObject) {
        val code = responseJson.optInt("code", -1)
        val message = responseJson.optString("message").ifBlank { "Unknown server error" }
        if (code != 200) throw ApiException(code, message)
    }
}
