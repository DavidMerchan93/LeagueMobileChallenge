package com.davidmerchan.domain.model

data class UserModel(
    val id: Int,
    val name: String,
    val address: AddressModel,
    val avatar: String,
    val company: CompanyModel,
    val email: String,
    val phone: String,
    val username: String,
    val website: String
)

data class AddressModel(
    val city: String,
    val geo: LocationModel,
    val street: String,
    val suite: String,
    val zipcode: String
)

data class LocationModel(
    val lat: String,
    val lng: String
)

data class CompanyModel(
    val bs: String,
    val catchPhrase: String,
    val name: String
)
