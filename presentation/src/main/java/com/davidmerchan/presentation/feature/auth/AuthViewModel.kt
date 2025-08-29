package com.davidmerchan.presentation.feature.auth

import androidx.lifecycle.viewModelScope
import com.davidmerchan.domain.useCase.GetAccessTokenUseCase
import com.davidmerchan.domain.useCase.SaveTokenUseCase
import com.davidmerchan.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class AuthViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val saveAccessTokenUseCase: SaveTokenUseCase
) : BaseViewModel<AuthContract.State>(AuthContract.State()) {

    init {
        checkAuthState()
    }

    fun handleEvent(event: AuthContract.Event) {
        when (event) {
            is AuthContract.Event.Logout -> logout()
        }
    }

    private fun checkAuthState() {
        mutableState.update { it.copy(isLoading = true, authState = AuthState.CHECKING) }

        viewModelScope.launch {
            getAccessTokenUseCase().onSuccess { result ->
                if (result.isNullOrBlank().not()) {
                    mutableState.update { it.copy(isLoading = false, authState = AuthState.AUTHENTICATED) }
                } else {
                    mutableState.update { it.copy(isLoading = false, authState = AuthState.UNAUTHENTICATED) }
                }
            }.onFailure {
                mutableState.update { it.copy(isLoading = false, authState = AuthState.UNAUTHENTICATED) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            saveAccessTokenUseCase("").onSuccess {
                mutableState.update { it.copy(isLoading = false, authState = AuthState.UNAUTHENTICATED) }
            }.onFailure {
                mutableState.update { it.copy(isLoading = false, authState = AuthState.UNAUTHENTICATED) }
            }
        }
    }
}
