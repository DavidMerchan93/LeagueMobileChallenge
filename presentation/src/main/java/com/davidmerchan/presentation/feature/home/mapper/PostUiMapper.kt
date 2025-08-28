package com.davidmerchan.presentation.feature.home.mapper

import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.presentation.feature.home.model.PostUiModel

object PostUiMapper {
    fun PostModel.toUi(): PostUiModel = PostUiModel(
        id = id,
        title = title,
        avatar = "",
        userName = "",
        description = body
    )
}
