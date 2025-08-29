package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.repository.TokenRepository

fun interface SaveTokenUseCase : suspend (String) -> Result<Boolean>

internal suspend fun saveToken(
    token: String,
    tokenRepository: TokenRepository,
): Result<Boolean> = tokenRepository.saveToken(token)
