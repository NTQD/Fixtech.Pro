package vn.vibe.booking.data.remote

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

class ApiDebugInterceptor(
    private val logger: (String) -> Unit = { println(it) }
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val headers = request.headers
            .names()
            .associateWith { name -> request.header(name).orEmpty() }
            .filterValues { it.isNotBlank() }

        val bodyText = request.body?.asUtf8String()
        val curl = when {
            request.method.equals("GET", ignoreCase = true) -> CurlDebug.get(url, headers)
            bodyText != null -> CurlDebug.post(url, bodyText, headers)
            else -> CurlDebug.get(url, headers)
        }

        logger(curl)
        bodyText?.let { logger("[API-DEBUG] ${CurlDebug.prettyJson(it)}") }

        return chain.proceed(request)
    }

    private fun RequestBody?.asUtf8String(): String? {
        if (this == null) return null
        val buffer = Buffer()
        writeTo(buffer)
        return buffer.readUtf8().takeIf { it.isNotBlank() }
    }
}
