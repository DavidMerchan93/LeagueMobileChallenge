package com.davidmerchan.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object LoginRoute

@Serializable
object HomeRoute

@Serializable
data class DetailRoute(
    val userId: Int
)
