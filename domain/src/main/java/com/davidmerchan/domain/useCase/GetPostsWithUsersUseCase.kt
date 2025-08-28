package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.model.PostWithUserModel
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.domain.repository.UserRepository

fun interface GetPostsWithUsersUseCase : suspend () -> Result<List<PostWithUserModel>>

internal suspend fun getPostsWithUsers(
    postRepository: PostRepository,
    userRepository: UserRepository
): Result<List<PostWithUserModel>> {
    val posts = postRepository.getPosts()
    val users = userRepository.getUsers()

    return runCatching {
        if (posts.isSuccess && users.isSuccess) {
            posts.getOrNull()?.map { post ->
                val user = users.getOrNull()?.firstOrNull { it.id == post.userId }

                PostWithUserModel(
                    id = post.id,
                    userId = post.userId,
                    title = post.title,
                    description = post.body,
                    avatar = user?.avatar.orEmpty(),
                    userName = user?.name.orEmpty()
                )
            } ?: emptyList()

        } else if (posts.isSuccess) {
            posts.getOrNull()?.map {
                PostWithUserModel(
                    id = it.id,
                    userId = it.userId,
                    title = it.title,
                    description = it.body,
                    avatar = "",
                    userName = ""
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }
}
