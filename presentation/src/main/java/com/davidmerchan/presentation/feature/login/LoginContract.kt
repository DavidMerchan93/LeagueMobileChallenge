package com.davidmerchan.presentation.feature.login

internal sealed interface LoginContract {
    data class State(
        val isLoading: Boolean = false,
        val isSuccessLogin: Boolean = false,
        val isError: Boolean = false,
    ) : LoginContract

    sealed interface Event : LoginContract {
        data class Login(
            val username: String,
            val password: String,
        ) : Event
    }
}
