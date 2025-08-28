package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.repository.AuthRepository

fun interface AuthUserCase: suspend (String, String) -> Result<Boolean>

internal suspend fun authUser(
    username: String = "",
    password: String = "",
    authRepository: AuthRepository,
    saveTokenUseCase: SaveTokenUseCase
): Result<Boolean> {
    val result = authRepository.login(username, password)

    return when {
        result.isSuccess && result.getOrNull()?.isNotEmpty() == true -> {
            saveTokenUseCase(result.getOrNull()!!)
        }

        result.isFailure -> {
            throw result.exceptionOrNull()!!
        }

        else -> {
            Result.failure(Exception("Unknown error"))
        }
    }
}
