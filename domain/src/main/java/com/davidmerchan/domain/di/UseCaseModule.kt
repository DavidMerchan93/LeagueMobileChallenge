package com.davidmerchan.domain.di

import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.domain.repository.TokenRepository
import com.davidmerchan.domain.repository.UserRepository
import com.davidmerchan.domain.useCase.AuthUserCase
import com.davidmerchan.domain.useCase.GetPostsUseCase
import com.davidmerchan.domain.useCase.GetPostsWithUsersUseCase
import com.davidmerchan.domain.useCase.GetUsersUseCase
import com.davidmerchan.domain.useCase.SaveTokenUseCase
import com.davidmerchan.domain.useCase.authUser
import com.davidmerchan.domain.useCase.getPosts
import com.davidmerchan.domain.useCase.getPostsWithUsers
import com.davidmerchan.domain.useCase.getUsers
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
    fun provideSaveTokenUseCase(tokenRepository: TokenRepository): SaveTokenUseCase {
        return SaveTokenUseCase { token -> saveToken(token, tokenRepository) }
    }

    @Provides
    @Singleton
    fun provideGetUsersUseCase(userRepository: UserRepository): GetUsersUseCase {
        return GetUsersUseCase { getUsers(userRepository) }
    }

    @Provides
    @Singleton
    fun provideGetPostsUseCase(postRepository: PostRepository): GetPostsUseCase {
        return GetPostsUseCase { getPosts(postRepository) }
    }

    @Provides
    @Singleton
    fun provideGetPostsWithUsersUseCase(
        postRepository: PostRepository,
        userRepository: UserRepository
    ): GetPostsWithUsersUseCase {
        return GetPostsWithUsersUseCase { getPostsWithUsers(postRepository, userRepository) }
    }
}
