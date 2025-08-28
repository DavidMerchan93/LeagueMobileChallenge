package com.davidmerchan.domain.model

data class PostWithUserModel(
    val id: Int,
    val userId: Int,
    val title: String,
    val description: String,
    val avatar: String,
    val userName: String
)
