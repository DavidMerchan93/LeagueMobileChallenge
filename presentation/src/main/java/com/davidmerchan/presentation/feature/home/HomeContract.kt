package com.davidmerchan.presentation.feature.home

import com.davidmerchan.presentation.feature.home.model.PostUiModel

internal interface HomeContract {
    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val items: List<PostUiModel> = emptyList()
    ): HomeContract
}
