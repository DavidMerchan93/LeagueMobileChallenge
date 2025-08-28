package com.davidmerchan.data.repository

import com.davidmerchan.data.mapper.UserMapper.toDomain
import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository
import com.davidmerchan.network.api.UserApi
import com.davidmerchan.network.util.safeApiCall
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi
) : UserRepository {
    override suspend fun getUsers(): Result<List<UserModel>> {
        return safeApiCall { userApi.getUsers().map { it.toDomain() } }
    }

    override suspend fun getUserById(userId: Int): Result<UserModel?> {
        TODO("Not yet implemented")
    }
}
