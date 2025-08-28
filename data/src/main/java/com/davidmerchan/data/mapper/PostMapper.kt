package com.davidmerchan.data.mapper

import com.davidmerchan.database.entities.PostEntity
import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.network.dto.PostDto

internal object PostMapper {
    fun PostDto.toDomain() = PostModel(
        id = id,
        userId = userId,
        title = title,
        body = body
    )

    fun PostEntity.toDomain() = PostModel(
        id = id,
        userId = userId,
        title = title,
        body = body
    )

    fun PostModel.toEntity() = PostEntity(
        id = id,
        userId = userId,
        title = title,
        body = body
    )
}
