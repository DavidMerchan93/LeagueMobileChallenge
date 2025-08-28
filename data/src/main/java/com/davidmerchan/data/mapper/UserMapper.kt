package com.davidmerchan.data.mapper

import com.davidmerchan.domain.model.AddressModel
import com.davidmerchan.domain.model.CompanyModel
import com.davidmerchan.domain.model.LocationModel
import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.network.dto.AddressDto
import com.davidmerchan.network.dto.CompanyDto
import com.davidmerchan.network.dto.LocationDto
import com.davidmerchan.network.dto.UserDto

object UserMapper {
    fun UserDto.toDomain(): UserModel = UserModel(
        id = id,
        name = name,
        address = address.toDomain(),
        avatar = avatar,
        company = company.toDomain(),
        email = email,
        phone = phone,
        username = username,
        website = website
    )

    fun AddressDto.toDomain(): AddressModel = AddressModel(
        city = city,
        street = street,
        suite = suite,
        zipcode = zipcode,
        geo = geo.toDomain()
    )

    fun LocationDto.toDomain(): LocationModel = LocationModel(
        lat = lat,
        lng = lng
    )

    fun CompanyDto.toDomain(): CompanyModel = CompanyModel(
        bs = bs,
        catchPhrase = catchPhrase,
        name = name
    )
}
