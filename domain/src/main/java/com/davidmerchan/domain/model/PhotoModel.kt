package com.davidmerchan.domain.model

data class PhotoModel(
    val id: Int,
    val title: String,
    val url: String,
    val albumId: Int,
    val thumbnailUrl: String
)
