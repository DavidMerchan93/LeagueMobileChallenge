package com.davidmerchan.network.api

import com.davidmerchan.network.dto.AuthDto
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthApi {
    @GET("login")
    suspend fun login(@Header("Authorization") credentials: String?): AuthDto
}


/**
 * Overloaded Login API extension function to handle authorization header encoding
 */
suspend fun AuthApi.login(username: String, password: String) = login(
    "Basic " + android.util.Base64.encodeToString(
        "$username:$password".toByteArray(),
        android.util.Base64.NO_WRAP
    )
)
