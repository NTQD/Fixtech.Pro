package vn.vibe.booking.data.remote

import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    fun parseObject(raw: String): JSONObject = try {
        JSONObject(raw)
    } catch (e: JSONException) {
        throw IOException("Invalid server response", e)
    }

    fun requireSuccess(responseJson: JSONObject) {
        val code = responseJson.optInt("code", -1)
        val message = responseJson.optString("message").ifBlank { "Unknown server error" }
        if (code != 200) throw ApiException(code, message)
    }
}
