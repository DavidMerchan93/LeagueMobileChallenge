package com.davidmerchan.data.repository

import com.davidmerchan.data.mapper.PostMapper.toDomain
import com.davidmerchan.database.storage.Storage
import com.davidmerchan.database.storage.StorageConstants.API_KEY
import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.network.api.PostApi
import com.davidmerchan.network.util.safeApiCall
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi,
    private val storage: Storage
) : PostRepository {
    override suspend fun getPosts(): Result<List<PostModel>> {
        val accessToken = storage.readSecureString(API_KEY).firstOrNull().orEmpty()
        return safeApiCall { postApi.getPosts(accessToken).map { it.toDomain() } }
    }
}
