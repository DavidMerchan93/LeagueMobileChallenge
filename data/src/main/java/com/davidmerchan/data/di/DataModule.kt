package com.davidmerchan.data.di

import com.davidmerchan.data.repository.AuthRepositoryImpl
import com.davidmerchan.data.repository.UserRepositoryImpl
import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}
