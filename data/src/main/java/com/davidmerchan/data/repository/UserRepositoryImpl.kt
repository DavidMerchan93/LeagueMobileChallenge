package com.davidmerchan.data.repository

import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository
import com.davidmerchan.network.api.UserApi

class UserRepositoryImpl(
    private val userApi: UserApi
): UserRepository {
    override suspend fun getUsers(): List<UserModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserById(userId: Int): UserModel? {
        TODO("Not yet implemented")
    }
}
