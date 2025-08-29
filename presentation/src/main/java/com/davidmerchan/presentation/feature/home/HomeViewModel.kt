package com.davidmerchan.presentation.feature.home

import androidx.lifecycle.viewModelScope
import com.davidmerchan.domain.useCase.GetPostsWithUsersUseCase
import com.davidmerchan.presentation.mapper.PostUiMapper.toUi
import com.davidmerchan.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel
    @Inject
    constructor(
        private val getPostsUseCase: GetPostsWithUsersUseCase,
    ) : BaseViewModel<HomeContract.State>(HomeContract.State()) {
        init {
            getPosts()
        }

        private fun getPosts() {
            mutableState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                getPostsUseCase()
                    .onSuccess { result ->
                        mutableState.update { state ->
                            state.copy(
                                isLoading = false,
                                items = result.map { it.toUi() },
                            )
                        }
                    }.onFailure {
                        mutableState.update { it.copy(isLoading = false, isError = true) }
                    }
            }
        }
    }
