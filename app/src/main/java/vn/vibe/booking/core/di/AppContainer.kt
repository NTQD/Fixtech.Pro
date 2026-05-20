package vn.vibe.booking.core.di

import android.content.Context
import okhttp3.OkHttpClient
import vn.vibe.booking.core.network.AuthInterceptor
import vn.vibe.booking.data.local.TokenDataStore
import vn.vibe.booking.data.remote.ApiDebugInterceptor
import vn.vibe.booking.data.remote.AuthApi
import vn.vibe.booking.data.remote.BookingApi
import vn.vibe.booking.data.remote.RepairServiceApi
import vn.vibe.booking.data.remote.ReviewApi
import vn.vibe.booking.data.remote.ServiceCategoryApi
import vn.vibe.booking.data.repository.AuthRepositoryImpl
import vn.vibe.booking.data.repository.UserRepositoryImpl
import vn.vibe.booking.domain.repository.AuthRepository
import vn.vibe.booking.domain.repository.TokenRepository
import vn.vibe.booking.domain.repository.UserRepository
import vn.vibe.booking.domain.usecase.LoginUseCase
import vn.vibe.booking.domain.usecase.ObserveAuthStateUseCase
import vn.vibe.booking.domain.usecase.RegisterUseCase

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val tokenRepository: TokenRepository = TokenDataStore(appContext)

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { runCatching { kotlinx.coroutines.runBlocking { tokenRepository.getCurrentToken() } }.getOrNull() })
            .addInterceptor(ApiDebugInterceptor())
            .build()
    }

    private val authApi = AuthApi(okHttpClient)

    val serviceCategoryApi = ServiceCategoryApi(okHttpClient)
    val repairServiceApi = RepairServiceApi(okHttpClient)
    val bookingApi = BookingApi(okHttpClient)
    val reviewApi = ReviewApi(okHttpClient)

    val authRepository: AuthRepository = AuthRepositoryImpl(authApi)
    val userRepository: UserRepository = UserRepositoryImpl(authApi)

    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val observeAuthStateUseCase = ObserveAuthStateUseCase(tokenRepository)
}
