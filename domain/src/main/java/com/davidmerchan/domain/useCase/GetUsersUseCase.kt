package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<UserModel>> = runCatching { 
        userRepository.getUsers() 
    }
}
