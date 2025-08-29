package com.davidmerchan.domain.di

import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.domain.repository.TokenRepository
import com.davidmerchan.domain.repository.UserRepository
import com.davidmerchan.domain.useCase.AuthUserCase
import com.davidmerchan.domain.useCase.GetAccessTokenUseCase
import com.davidmerchan.domain.useCase.GetPostsWithUsersUseCase
import com.davidmerchan.domain.useCase.GetUserByIdUseCase
import com.davidmerchan.domain.useCase.SaveTokenUseCase
import com.davidmerchan.domain.useCase.authUser
import com.davidmerchan.domain.useCase.getAccessToken
import com.davidmerchan.domain.useCase.getPostsWithUsers
import com.davidmerchan.domain.useCase.getUserById
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
        saveTokenUseCase: SaveTokenUseCase,
    ): AuthUserCase =
        AuthUserCase { user, password ->
            authUser(
                username = user,
                password = password,
                authRepository = authRepository,
                saveTokenUseCase = saveTokenUseCase,
            )
        }

    @Provides
    @Singleton
    fun provideSaveTokenUseCase(tokenRepository: TokenRepository): SaveTokenUseCase =
        SaveTokenUseCase { token -> saveToken(token, tokenRepository) }

    @Provides
    @Singleton
    fun provideGetPostsWithUsersUseCase(
        postRepository: PostRepository,
        userRepository: UserRepository,
    ): GetPostsWithUsersUseCase = GetPostsWithUsersUseCase { getPostsWithUsers(postRepository, userRepository) }

    @Provides
    @Singleton
    fun provideGetUserByIdUseCase(userRepository: UserRepository): GetUserByIdUseCase =
        GetUserByIdUseCase { userId -> getUserById(userRepository, userId) }

    @Provides
    @Singleton
    fun provideGetAccessTokenUseCase(tokenRepository: TokenRepository): GetAccessTokenUseCase =
        GetAccessTokenUseCase { getAccessToken(tokenRepository) }
}
