package com.davidmerchan.network.api

import com.davidmerchan.network.dto.PostDto
import retrofit2.http.GET
import retrofit2.http.Header

interface PostApi {
    @GET("posts")
    suspend fun getPosts(
        @Header("x-access-token") accessToken: String,
    ): List<PostDto>
}
