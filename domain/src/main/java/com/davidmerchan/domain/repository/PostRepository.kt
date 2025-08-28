package com.davidmerchan.domain.repository

import com.davidmerchan.domain.model.PostModel

interface PostRepository {
    suspend fun getPosts(): Result<List<PostModel>>
}
