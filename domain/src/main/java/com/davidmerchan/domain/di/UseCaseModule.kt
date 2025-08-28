package com.davidmerchan.domain.di

import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.domain.useCase.AuthUserCase
import com.davidmerchan.domain.useCase.SaveTokenUseCase
import com.davidmerchan.domain.useCase.authUser
import com.davidmerchan.domain.useCase.saveToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Domain module for use cases.
 * Since use cases use @Inject constructors, Hilt will automatically provide them.
 * This module is kept for future domain-specific bindings if needed.
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideAuthUserCase(
        authRepository: AuthRepository,
        saveTokenUseCase: SaveTokenUseCase
    ): AuthUserCase {
        return AuthUserCase { user, password ->
            authUser(
                username = user,
                password = password,
                authRepository = authRepository,
                saveTokenUseCase = saveTokenUseCase
            )
        }
    }

    @Provides
    @Singleton
    fun provideSaveTokenUseCase(): SaveTokenUseCase {
        return SaveTokenUseCase { token -> saveToken(token) }
    }

}
