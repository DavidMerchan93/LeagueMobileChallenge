package com.davidmerchan.presentation.util

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel<State>(
    initialState: State
) : ViewModel() {
    @VisibleForTesting
    internal var stopTimeoutMillis: Long = 5_000

    val mutableState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = mutableState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis),
            initialValue = initialState,
        )

}
