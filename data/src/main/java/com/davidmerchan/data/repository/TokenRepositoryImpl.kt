package com.davidmerchan.data.repository

import com.davidmerchan.database.storage.Storage
import com.davidmerchan.database.storage.StorageConstants.API_KEY
import com.davidmerchan.domain.repository.TokenRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val storage: Storage
) : TokenRepository {
    override suspend fun getToken(): Result<String?> {
        return runCatching { storage.readSecureString(API_KEY).firstOrNull() }
    }


    override suspend fun saveToken(token: String): Result<Boolean> {
        return runCatching {
            storage.saveSecureString(API_KEY, token)
            true
        }
    }
}
