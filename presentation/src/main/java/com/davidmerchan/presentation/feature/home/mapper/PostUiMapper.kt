package com.davidmerchan.presentation.feature.home.mapper

import com.davidmerchan.domain.model.PostWithUserModel
import com.davidmerchan.presentation.feature.home.model.PostUiModel

object PostUiMapper {
    fun PostWithUserModel.toUi(): PostUiModel = PostUiModel(
        id = id,
        title = title,
        avatar = avatar,
        userName = userName,
        description = description
    )
}
