package vn.vibe.booking.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vn.vibe.booking.data.repository.AuthRepositoryImpl
import vn.vibe.booking.domain.repository.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        tokenDataStore: vn.vibe.booking.data.local.TokenDataStore
    ): vn.vibe.booking.domain.repository.TokenRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: vn.vibe.booking.data.repository.UserRepositoryImpl
    ): vn.vibe.booking.domain.repository.UserRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: vn.vibe.booking.data.repository.HomeRepositoryImpl
    ): vn.vibe.booking.data.repository.HomeRepository
}
