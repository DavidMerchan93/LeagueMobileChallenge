package com.davidmerchan.data.repository

import com.davidmerchan.data.mapper.PostMapper.toDomain
import com.davidmerchan.data.mapper.PostMapper.toEntity
import com.davidmerchan.database.dao.PostDao
import com.davidmerchan.database.storage.Storage
import com.davidmerchan.database.storage.StorageConstants.API_KEY
import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.network.api.PostApi
import com.davidmerchan.network.util.safeApiCall
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class PostRepositoryImpl
    @Inject
    constructor(
        private val postApi: PostApi,
        private val storage: Storage,
        private val postDao: PostDao,
    ) : PostRepository {
        override suspend fun getPosts(): Result<List<PostModel>> =
            safeApiCall {
                val posts = postDao.getAllPosts().map { it.toDomain() }

                posts.ifEmpty {
                    val accessToken = storage.readSecureString(API_KEY).firstOrNull().orEmpty()
                    val postsApi = postApi.getPosts(accessToken).map { it.toDomain() }

                    postDao.insertPosts(postsApi.map { it.toEntity() })
                    postsApi
                }
            }
    }
