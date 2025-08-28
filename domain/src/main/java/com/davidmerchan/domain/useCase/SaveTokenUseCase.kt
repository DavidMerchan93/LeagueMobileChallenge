package com.davidmerchan.domain.useCase

fun interface SaveTokenUseCase: suspend (String) -> Result<Boolean>

internal suspend fun saveToken(token: String): Result<Boolean> {
    return Result.success(true)
}
