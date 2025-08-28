package com.davidmerchan.presentation.feature.login

import androidx.lifecycle.viewModelScope
import com.davidmerchan.domain.useCase.AuthUserCase
import com.davidmerchan.presentation.util.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val authUserCase: AuthUserCase
): BaseViewModel<LoginContract.State>(LoginContract.State()) {

    fun handleEvent(event: LoginContract.Event) {
        when(event) {
            is LoginContract.Event.Login -> login(event.username, event.password)
        }
    }

    private fun login(username: String, password: String) {
        mutableState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val response = authUserCase(username, password)

            if (response.isSuccess) {
                mutableState.update { it.copy(successLogin = true, isLoading = false) }
            } else {
                mutableState.update { it.copy(error = true, isLoading = false) }
            }
        }
    }
}
