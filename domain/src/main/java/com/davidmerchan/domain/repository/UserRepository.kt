package com.davidmerchan.domain.repository

import com.davidmerchan.domain.model.UserModel

interface UserRepository {
    suspend fun getUsers(): List<UserModel>
    suspend fun getUserById(userId: Int): UserModel?
}
