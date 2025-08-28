package com.davidmerchan.domain.model

data class PostModel(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
