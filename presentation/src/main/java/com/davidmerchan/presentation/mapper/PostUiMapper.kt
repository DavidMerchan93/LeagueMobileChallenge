package com.davidmerchan.presentation.mapper

import com.davidmerchan.domain.model.PostWithUserModel
import com.davidmerchan.presentation.model.PostUiModel

object PostUiMapper {
    fun PostWithUserModel.toUi(): PostUiModel =
        PostUiModel(
            id = id,
            title = title,
            avatar = avatar,
            userName = userName,
            description = description,
            userId = userId,
        )
}
