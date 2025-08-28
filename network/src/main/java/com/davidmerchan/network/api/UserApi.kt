package com.davidmerchan.network.api

import com.davidmerchan.network.dto.UserDto
import retrofit2.http.GET

interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<UserDto>

}
