package com.davidmerchan.network.api

import com.davidmerchan.network.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("users")
    suspend fun getUsers(@Header("x-access-token") accessToken: String): List<UserDto>

}
