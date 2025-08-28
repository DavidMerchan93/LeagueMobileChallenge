package com.davidmerchan.data.repository

import com.davidmerchan.data.mapper.AuthMapper.toDomain
import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.network.api.AuthApi
import com.davidmerchan.network.api.login
import com.davidmerchan.network.util.safeApiCall

class AuthRepositoryImpl(
    private val authApi: AuthApi
): AuthRepository {
    override suspend fun login(username: String, password: String): Result<String> {
        return safeApiCall { authApi.login(username, password).toDomain() }
    }
}
