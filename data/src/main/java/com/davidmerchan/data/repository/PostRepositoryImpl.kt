package com.davidmerchan.data.repository

import com.davidmerchan.data.mapper.PostMapper.toDomain
import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.network.api.PostApi
import com.davidmerchan.network.util.safeApiCall
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi
) : PostRepository {
    override suspend fun getPosts(): Result<List<PostModel>> {
        return safeApiCall { postApi.getPosts().map { it.toDomain() } }
    }
}
