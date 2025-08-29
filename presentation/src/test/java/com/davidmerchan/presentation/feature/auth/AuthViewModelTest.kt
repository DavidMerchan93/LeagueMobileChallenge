package com.davidmerchan.presentation.feature.auth

import com.davidmerchan.domain.useCase.GetAccessTokenUseCase
import com.davidmerchan.domain.useCase.SaveTokenUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
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
    fun `initial state checks auth automatically with valid token`() =
        runTest {
            // Given
            val getAccessTokenUseCase = GetAccessTokenUseCase { Result.success("valid_token_123") }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }

            // When
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after check", finalState.isLoading)
            assertEquals("Should be authenticated", AuthState.AUTHENTICATED, finalState.authState)
            assertFalse("Should not have error", finalState.isError)
        }

    @Test
    fun `initial state checks auth automatically with no token`() =
        runTest {
            // Given
            val getAccessTokenUseCase = GetAccessTokenUseCase { Result.success(null) }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }

            // When
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after check", finalState.isLoading)
            assertEquals("Should be unauthenticated", AuthState.UNAUTHENTICATED, finalState.authState)
            assertFalse("Should not have error", finalState.isError)
        }

    @Test
    fun `initial state checks auth automatically with empty token`() =
        runTest {
            // Given
            val getAccessTokenUseCase = GetAccessTokenUseCase { Result.success("") }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }

            // When
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after check", finalState.isLoading)
            assertEquals("Should be unauthenticated", AuthState.UNAUTHENTICATED, finalState.authState)
            assertFalse("Should not have error", finalState.isError)
        }

    @Test
    fun `initial state checks auth automatically with blank token`() =
        runTest {
            // Given
            val getAccessTokenUseCase = GetAccessTokenUseCase { Result.success("   ") }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }

            // When
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after check", finalState.isLoading)
            assertEquals("Should be unauthenticated", AuthState.UNAUTHENTICATED, finalState.authState)
            assertFalse("Should not have error", finalState.isError)
        }

    @Test
    fun `loading state is set during auth check`() =
        runTest {
            // Given
            val getAccessTokenUseCase =
                GetAccessTokenUseCase {
                    kotlinx.coroutines.delay(50)
                    Result.success("token")
                }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }

            // When
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)

            // Advance just a little to check loading state
            testDispatcher.scheduler.advanceTimeBy(10)

            // Then
            val loadingState = viewModel.uiState.value
            assertTrue("Should be loading during auth check", loadingState.isLoading)
            assertEquals("Should be in checking state", AuthState.CHECKING, loadingState.authState)
            assertFalse("Should not have error during loading", loadingState.isError)
        }

    @Test
    fun `token retrieval error sets unauthenticated state`() =
        runTest {
            // Given
            val getAccessTokenUseCase =
                GetAccessTokenUseCase {
                    Result.failure(RuntimeException("Storage error"))
                }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }

            // When
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after error", finalState.isLoading)
            assertEquals("Should be unauthenticated on error", AuthState.UNAUTHENTICATED, finalState.authState)
            assertFalse("Should not have error flag", finalState.isError)
        }

    @Test
    fun `logout clears token and sets unauthenticated`() =
        runTest {
            // Given - Start with authenticated user
            val getAccessTokenUseCase = GetAccessTokenUseCase { Result.success("valid_token") }
            val saveAccessTokenUseCase =
                SaveTokenUseCase { token ->
                    if (token.isEmpty()) Result.success(true) else Result.success(false)
                }
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // Verify initially authenticated
            assertEquals("Should start authenticated", AuthState.AUTHENTICATED, viewModel.uiState.value.authState)

            // When
            viewModel.handleEvent(AuthContract.Event.Logout)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after logout", finalState.isLoading)
            assertEquals("Should be unauthenticated after logout", AuthState.UNAUTHENTICATED, finalState.authState)
            assertFalse("Should not have error", finalState.isError)
        }

    @Test
    fun `logout with save error still sets unauthenticated`() =
        runTest {
            // Given - Start with authenticated user
            val getAccessTokenUseCase = GetAccessTokenUseCase { Result.success("valid_token") }
            val saveAccessTokenUseCase =
                SaveTokenUseCase {
                    Result.failure(RuntimeException("Save error"))
                }
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // When
            viewModel.handleEvent(AuthContract.Event.Logout)
            advanceUntilIdle()

            // Then - Should still logout even if save fails
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after logout", finalState.isLoading)
            assertEquals("Should be unauthenticated after logout", AuthState.UNAUTHENTICATED, finalState.authState)
        }

    @Test
    fun `auth check transitions through correct states`() =
        runTest {
            // Given
            val getAccessTokenUseCase =
                GetAccessTokenUseCase {
                    kotlinx.coroutines.delay(30)
                    Result.success("valid_token")
                }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }

            // When
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)

            // Initial state should be checking with loading
            val initialState = viewModel.uiState.value
            assertTrue("Should be loading initially", initialState.isLoading)
            assertEquals("Should be in checking state", AuthState.CHECKING, initialState.authState)

            // Complete the auth check
            advanceUntilIdle()

            // Final state should be authenticated
            val finalState = viewModel.uiState.value
            assertFalse("Should not be loading after completion", finalState.isLoading)
            assertEquals("Should be authenticated", AuthState.AUTHENTICATED, finalState.authState)
            assertFalse("Should not have error", finalState.isError)
        }

    @Test
    fun `multiple logout calls are handled gracefully`() =
        runTest {
            // Given - Start with authenticated user
            val getAccessTokenUseCase = GetAccessTokenUseCase { Result.success("valid_token") }
            val saveAccessTokenUseCase = SaveTokenUseCase { Result.success(true) }
            val viewModel = AuthViewModel(getAccessTokenUseCase, saveAccessTokenUseCase)
            advanceUntilIdle()

            // First logout
            viewModel.handleEvent(AuthContract.Event.Logout)
            advanceUntilIdle()

            val firstLogoutState = viewModel.uiState.value
            assertEquals(
                "Should be unauthenticated after first logout",
                AuthState.UNAUTHENTICATED,
                firstLogoutState.authState,
            )

            // Second logout (should be no-op)
            viewModel.handleEvent(AuthContract.Event.Logout)
            advanceUntilIdle()

            val secondLogoutState = viewModel.uiState.value
            assertEquals("Should remain unauthenticated", AuthState.UNAUTHENTICATED, secondLogoutState.authState)
            assertFalse("Should not have error", secondLogoutState.isError)
        }
}
