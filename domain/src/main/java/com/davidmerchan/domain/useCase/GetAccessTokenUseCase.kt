package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.repository.TokenRepository

fun interface GetAccessTokenUseCase: suspend () -> Result<String?>

internal suspend fun getAccessToken(tokenRepository: TokenRepository): Result<String?> {
    return tokenRepository.getToken()
}
