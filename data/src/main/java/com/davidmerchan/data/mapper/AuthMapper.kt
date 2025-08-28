package com.davidmerchan.data.mapper

import com.davidmerchan.network.dto.AuthDto

object AuthMapper {
    fun AuthDto.toDomain(): String = apiKey
}
