package com.davidmerchan.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    @SerialName("api_key")
    val apiKey: String
)
