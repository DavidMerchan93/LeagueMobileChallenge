package com.davidmerchan.data.repository

import com.davidmerchan.data.mapper.UserMapper.toDomain
import com.davidmerchan.data.mapper.UserMapper.toEntity
import com.davidmerchan.database.dao.UserDao
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
    private val storage: Storage,
    private val userDao: UserDao
) : UserRepository {
    override suspend fun getUsers(): Result<List<UserModel>> {
        return safeApiCall {
            val users = userDao.getAllUsers().map { it.toDomain() }

            users.ifEmpty {
                val accessToken = storage.readSecureString(API_KEY).firstOrNull().orEmpty()
                val usersApi = userApi.getUsers(accessToken).map { it.toDomain() }

                userDao.insertUsers(usersApi.map { it.toEntity() })
                usersApi
            }
        }
    }

    override suspend fun getUserById(userId: Int): Result<UserModel?> {
        return safeApiCall { userDao.getUserById(userId)?.toDomain() }
    }
}
