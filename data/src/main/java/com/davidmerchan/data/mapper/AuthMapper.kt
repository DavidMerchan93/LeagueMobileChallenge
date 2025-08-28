package com.davidmerchan.data.mapper

import com.davidmerchan.network.dto.AuthDto

internal object AuthMapper {
    fun AuthDto.toDomain(): String = apiKey
}
