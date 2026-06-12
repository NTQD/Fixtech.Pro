package vn.vibe.booking.di

import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import vn.vibe.booking.core.config.AppConfig
import vn.vibe.booking.core.network.AuthInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAdminApi(client: OkHttpClient): vn.vibe.booking.data.remote.AdminApi = vn.vibe.booking.data.remote.AdminApi(client)

    @Provides
    @Singleton
    fun provideServiceCategoryApi(client: OkHttpClient): vn.vibe.booking.data.remote.ServiceCategoryApi = vn.vibe.booking.data.remote.ServiceCategoryApi(client)

    @Provides
    @Singleton
    fun provideRepairServiceApi(client: OkHttpClient): vn.vibe.booking.data.remote.RepairServiceApi = vn.vibe.booking.data.remote.RepairServiceApi(client)

    @Provides
    @Singleton
    fun provideBookingApi(client: OkHttpClient): vn.vibe.booking.data.remote.BookingApi = vn.vibe.booking.data.remote.BookingApi(client)

    @Provides
    @Singleton
    fun provideReviewApi(client: OkHttpClient): vn.vibe.booking.data.remote.ReviewApi = vn.vibe.booking.data.remote.ReviewApi(client)
}
