package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository

fun interface GetUsersUseCase : suspend () -> Result<List<UserModel>>

suspend fun getUsers(userRepository: UserRepository): Result<List<UserModel>> {
    return userRepository.getUsers()
}
