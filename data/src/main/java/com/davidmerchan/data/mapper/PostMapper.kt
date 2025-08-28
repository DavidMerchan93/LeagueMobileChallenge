package com.davidmerchan.data.mapper

import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.network.dto.PostDto

object PostMapper {
    fun PostDto.toDomain() = PostModel(
        id = id,
        userId = userId,
        title = title,
        body = body
    )
}
