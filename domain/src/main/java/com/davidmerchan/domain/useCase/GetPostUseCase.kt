package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.domain.repository.PostRepository

fun interface GetPostsUseCase : suspend () -> Result<List<PostModel>>

suspend fun getPosts(postRepository: PostRepository): Result<List<PostModel>> {
    return postRepository.getPosts()
}
