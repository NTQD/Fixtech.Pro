package vn.vibe.booking.data.remote

import okhttp3.Request
import org.json.JSONObject

object CurlDebug {
    fun request(request: Request): String {
        return buildString {
            append("curl --location '")
            append(request.url)
            append("'")

            if (request.method.isNotBlank()) {
                append(" \\\n")
                append("--request '")
                append(request.method)
                append("'")
            }

            request.headers.names().forEach { name ->
                val value = request.header(name).orEmpty()
                append(" \\\n")
                append("--header '")
                append(name)
                append(": ")
                append(escapeSingleQuotes(value))
                append("'")
            }

            val bodyText = requestBodyToString(request)
            if (bodyText.isNotBlank()) {
                append(" \\\n")
                append("--data-raw '")
                append(escapeSingleQuotes(bodyText))
                append("'")
            }
        }.trimEnd()
    }

    private fun requestBodyToString(request: Request): String {
        val body = request.body ?: return ""
        val buffer = okio.Buffer()
        body.writeTo(buffer)
        return buffer.readUtf8()
    }

    private fun escapeSingleQuotes(value: String): String {
        return value.replace("'", "'\\''")
    }

    fun prettyJson(raw: String): String = try {
        JSONObject(raw).toString(2)
    } catch (_: Exception) {
        raw
    }
}
