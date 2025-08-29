package com.davidmerchan.presentation.feature.auth

enum class AuthState {
    CHECKING,
    AUTHENTICATED,
    UNAUTHENTICATED
}

internal sealed interface AuthContract {
    data class State(
        val isLoading: Boolean = false,
        val authState: AuthState = AuthState.UNAUTHENTICATED,
        val isError: Boolean = false
    )

    sealed interface Event {
        object Logout : Event
    }
}
