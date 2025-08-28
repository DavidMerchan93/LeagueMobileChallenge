package com.davidmerchan.presentation.mapper

import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.presentation.model.UserUiModel

object UserUiMapper {
    fun UserModel.toUi(): UserUiModel {
        return UserUiModel(
            id = id,
            name = name,
            username = username,
            email = email,
            phone = phone,
            website = website
        )
    }
}
