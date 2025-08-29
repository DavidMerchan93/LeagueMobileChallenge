package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository

fun interface GetUserByIdUseCase : suspend (Int) -> Result<UserModel?>

internal suspend fun getUserById(
    userRepository: UserRepository,
    userId: Int,
): Result<UserModel?> = userRepository.getUserById(userId)
