package com.davidmerchan.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val address: AddressDto,
    val avatar: String,
    val company: CompanyDto,
    val email: String,
    val phone: String,
    val username: String,
    val website: String,
)

@Serializable
data class AddressDto(
    val city: String,
    val geo: LocationDto,
    val street: String,
    val suite: String,
    val zipcode: String,
)

@Serializable
data class LocationDto(
    val lat: String,
    val lng: String,
)

@Serializable
data class CompanyDto(
    val bs: String,
    val catchPhrase: String,
    val name: String,
)
