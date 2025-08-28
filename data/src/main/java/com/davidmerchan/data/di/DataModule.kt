package com.davidmerchan.data.di

import com.davidmerchan.data.repository.AuthRepositoryImpl
import com.davidmerchan.data.repository.PostRepositoryImpl
import com.davidmerchan.data.repository.TokenRepositoryImpl
import com.davidmerchan.data.repository.UserRepositoryImpl
import com.davidmerchan.database.storage.Storage
import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.domain.repository.TokenRepository
import com.davidmerchan.domain.repository.UserRepository
import com.davidmerchan.network.api.AuthApi
import com.davidmerchan.network.api.PostApi
import com.davidmerchan.network.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun providesUserRepository(userApi: UserApi, storage: Storage): UserRepository =
        UserRepositoryImpl(userApi, storage)

    @Provides
    @Singleton
    fun providesAuthRepository(authApi: AuthApi): AuthRepository = AuthRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun providePostRepository(postApi: PostApi, storage: Storage): PostRepository = PostRepositoryImpl(postApi, storage)

    @Provides
    @Singleton
    fun provideTokenRepository(storage: Storage): TokenRepository = TokenRepositoryImpl(storage)
}
