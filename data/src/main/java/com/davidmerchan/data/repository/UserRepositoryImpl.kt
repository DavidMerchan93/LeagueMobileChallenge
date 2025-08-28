package com.davidmerchan.data.repository

import com.davidmerchan.data.mapper.UserMapper.toDomain
import com.davidmerchan.database.storage.Storage
import com.davidmerchan.database.storage.StorageConstants.API_KEY
import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository
import com.davidmerchan.network.api.UserApi
import com.davidmerchan.network.util.safeApiCall
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,
    private val storage: Storage
) : UserRepository {
    override suspend fun getUsers(): Result<List<UserModel>> {
        val accessToken = storage.readSecureString(API_KEY).firstOrNull().orEmpty()
        return safeApiCall { userApi.getUsers(accessToken).map { it.toDomain() } }
    }

    override suspend fun getUserById(userId: Int): Result<UserModel?> {
        TODO("Not yet implemented")
    }
}
