package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.repository.AuthRepository

class AuthUserCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<String> {
        return authRepository.login(username, password)
    }
}
