package vn.vibe.booking.core.di

import android.content.Context
import okhttp3.OkHttpClient
import vn.vibe.booking.core.network.AuthInterceptor
import vn.vibe.booking.data.local.TokenDataStore
import vn.vibe.booking.data.remote.AuthApi
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
    private val api = AuthApi()

    val authRepository: AuthRepository = AuthRepositoryImpl(api)
    val userRepository: UserRepository = UserRepositoryImpl(api)

    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val observeAuthStateUseCase = ObserveAuthStateUseCase(tokenRepository)

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { runCatching { kotlinx.coroutines.runBlocking { tokenRepository.getCurrentToken() } }.getOrNull() })
            .build()
    }
}
