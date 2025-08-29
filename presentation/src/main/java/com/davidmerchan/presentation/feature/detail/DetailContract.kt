package com.davidmerchan.presentation.feature.detail

import com.davidmerchan.presentation.model.UserUiModel

internal sealed interface DetailContract {
    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val user: UserUiModel? = null,
    ) : DetailContract

    sealed interface Event : DetailContract {
        data class GetUserById(
            val userId: Int,
        ) : Event
    }
}
