package com.davidmerchan.network.api

import com.davidmerchan.network.dto.PostDto
import retrofit2.http.GET

interface PostApi {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}
