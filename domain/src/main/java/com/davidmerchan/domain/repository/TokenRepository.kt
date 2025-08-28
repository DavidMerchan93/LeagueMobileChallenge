package com.davidmerchan.domain.repository

interface TokenRepository {
    suspend fun saveToken(token: String): Result<Boolean>
    suspend fun getToken(): Result<String?>
}
