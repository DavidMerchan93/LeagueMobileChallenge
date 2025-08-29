package com.davidmerchan.presentation.feature.login

import com.davidmerchan.domain.useCase.AuthUserCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct values`() = runTest {
        // Given
        val authUserCase = AuthUserCase { _, _ -> Result.success(true) }
        val viewModel = LoginViewModel(authUserCase)

        // When
        advanceUntilIdle()
        val state = viewModel.uiState.value

        // Then
        assertFalse("Initial loading should be false", state.isLoading)
        assertFalse("Initial success should be false", state.isSuccessLogin)
        assertFalse("Initial error should be false", state.isError)
    }

    @Test
    fun `successful login flow works correctly`() = runTest {
        // Given
        val authUserCase = AuthUserCase { _, _ -> Result.success(true) }
        val viewModel = LoginViewModel(authUserCase)

        // When
        viewModel.handleEvent(LoginContract.Event.Login("user", "pass"))
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertFalse("Loading should be false after completion", finalState.isLoading)
        assertTrue("Success should be true", finalState.isSuccessLogin)
        assertFalse("Error should be false", finalState.isError)
    }

    @Test
    fun `failed login shows error state`() = runTest {
        // Given
        val authUserCase = AuthUserCase { _, _ -> Result.failure(Exception("Login failed")) }
        val viewModel = LoginViewModel(authUserCase)

        // When
        viewModel.handleEvent(LoginContract.Event.Login("wrong", "wrong"))
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertFalse("Loading should be false after completion", finalState.isLoading)
        assertFalse("Success should be false", finalState.isSuccessLogin)
        assertTrue("Error should be true", finalState.isError)
    }

    @Test
    fun `network error shows error state`() = runTest {
        // Given
        val authUserCase = AuthUserCase { _, _ -> Result.failure(RuntimeException("Network error")) }
        val viewModel = LoginViewModel(authUserCase)

        // When
        viewModel.handleEvent(LoginContract.Event.Login("user", "pass"))
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertFalse("Loading should be false after error", finalState.isLoading)
        assertFalse("Success should be false on error", finalState.isSuccessLogin)
        assertTrue("Error should be true on network error", finalState.isError)
    }

    @Test
    fun `empty credentials show error state`() = runTest {
        // Given
        val authUserCase = AuthUserCase { username, password ->
            when {
                username.isEmpty() || password.isEmpty() -> Result.failure(Exception("Empty credentials"))
                else -> Result.success(true)
            }
        }
        val viewModel = LoginViewModel(authUserCase)

        // When
        viewModel.handleEvent(LoginContract.Event.Login("", ""))
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertFalse("Loading should be false after error", finalState.isLoading)
        assertFalse("Success should be false on error", finalState.isSuccessLogin)
        assertTrue("Error should be true on empty credentials", finalState.isError)
    }

    @Test
    fun `multiple login attempts reset previous states`() = runTest {
        // Given
        val authUserCase = AuthUserCase { username, password ->
            when {
                username == "valid" && password == "valid" -> Result.success(true)
                else -> Result.failure(Exception("Invalid credentials"))
            }
        }
        val viewModel = LoginViewModel(authUserCase)

        // First login - failure
        viewModel.handleEvent(LoginContract.Event.Login("invalid", "invalid"))
        advanceUntilIdle()

        val firstState = viewModel.uiState.value
        assertTrue("First attempt should show error", firstState.isError)
        assertFalse("First attempt should not show success", firstState.isSuccessLogin)

        // Second login - success (should reset previous error)
        viewModel.handleEvent(LoginContract.Event.Login("valid", "valid"))
        advanceUntilIdle()

        val secondState = viewModel.uiState.value
        assertFalse("Second attempt should not show error", secondState.isError)
        assertTrue("Second attempt should show success", secondState.isSuccessLogin)
        assertFalse("Should not be loading after completion", secondState.isLoading)
    }
}
