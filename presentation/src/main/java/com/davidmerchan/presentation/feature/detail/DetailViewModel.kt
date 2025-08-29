package com.davidmerchan.presentation.feature.detail

import androidx.lifecycle.viewModelScope
import com.davidmerchan.domain.useCase.GetUserByIdUseCase
import com.davidmerchan.presentation.mapper.UserUiMapper.toUi
import com.davidmerchan.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DetailViewModel
    @Inject
    constructor(
        private val getUserByIdUseCase: GetUserByIdUseCase,
    ) : BaseViewModel<DetailContract.State>(DetailContract.State()) {
        fun handleEvent(event: DetailContract.Event) {
            when (event) {
                is DetailContract.Event.GetUserById -> getUserById(event.userId)
            }
        }

        private fun getUserById(userId: Int) {
            mutableState.update { it.copy(isLoading = true, isError = false) }
            viewModelScope.launch {
                getUserByIdUseCase(userId)
                    .onSuccess { user ->
                        mutableState.update { it.copy(isLoading = false, user = user?.toUi(), isError = false) }
                    }.onFailure {
                        mutableState.update { it.copy(isLoading = false, isError = true, user = null) }
                    }
            }
        }
    }
