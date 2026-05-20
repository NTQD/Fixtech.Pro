package vn.vibe.booking.data.remote

import org.json.JSONObject

object CurlDebug {
    fun post(url: String, body: String, headers: Map<String, String> = emptyMap()): String {
        return buildString {
            append("curl --location '")
            append(url)
            append("' \\\n")
            headers.forEach { (key, value) ->
                append("--header '")
                append(key)
                append(": ")
                append(escapeSingleQuotes(value))
                append("' \\\n")
            }
            append("--data-raw'")
            append(escapeSingleQuotes(body))
            append("'")
        }
    }

    fun get(url: String, headers: Map<String, String> = emptyMap()): String {
        return buildString {
            append("curl --location '")
            append(url)
            append("' \\\n")
            headers.forEach { (key, value) ->
                append("--header '")
                append(key)
                append(": ")
                append(escapeSingleQuotes(value))
                append("' \\\n")
            }
        }.trimEnd('\n', ' ', '\\')
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
