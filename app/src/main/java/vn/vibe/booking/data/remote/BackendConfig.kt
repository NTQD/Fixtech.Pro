package vn.vibe.booking.data.remote

import vn.vibe.booking.BuildConfig

object BackendConfig {
    val BASE_URL = BuildConfig.BASE_URL.removeSuffix("/")
}
