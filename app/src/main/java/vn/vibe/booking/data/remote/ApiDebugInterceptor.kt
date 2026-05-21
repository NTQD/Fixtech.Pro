package vn.vibe.booking.data.remote

import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

class ApiDebugInterceptor(
    private val logger: (String) -> Unit = { println(it) }
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        logger("[API-DEBUG][CURL] ${CurlDebug.request(request)}")

        request.body?.asUtf8String()?.takeIf { it.isNotBlank() }?.let {
            logger("[API-DEBUG][BODY] ${CurlDebug.prettyJson(it)}")
        }

        return chain.proceed(request)
    }

    private fun RequestBody?.asUtf8String(): String? {
        if (this == null) return null
        val buffer = Buffer()
        writeTo(buffer)
        return buffer.readUtf8()
    }
}
