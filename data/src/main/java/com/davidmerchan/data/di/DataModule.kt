package com.davidmerchan.data.di

import com.davidmerchan.data.repository.AuthRepositoryImpl
import com.davidmerchan.data.repository.PostRepositoryImpl
import com.davidmerchan.data.repository.UserRepositoryImpl
import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.domain.repository.PostRepository
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
    fun providesUserRepository(userApi: UserApi): UserRepository = UserRepositoryImpl(userApi)

    @Provides
    @Singleton
    fun providesAuthRepository(authApi: AuthApi): AuthRepository = AuthRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun providePostRepository(postApi: PostApi): PostRepository = PostRepositoryImpl(postApi)
}
