package com.davidmerchan.presentation.feature.detail

import com.davidmerchan.domain.model.AddressModel
import com.davidmerchan.domain.model.CompanyModel
import com.davidmerchan.domain.model.LocationModel
import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.useCase.GetUserByIdUseCase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
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
    fun `initial state has correct values`() =
        runTest {
            // Given
            val getUserByIdUseCase = GetUserByIdUseCase { Result.success(null) }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // When
            advanceUntilIdle()
            val state = viewModel.uiState.value

            // Then
            assertFalse("Initial loading should be false", state.isLoading)
            assertFalse("Initial error should be false", state.isError)
            assertNull("Initial user should be null", state.user)
        }

    @Test
    fun `successful user loading updates state correctly`() =
        runTest {
            // Given
            val expectedUser =
                UserModel(
                    id = 1,
                    name = "John Doe",
                    username = "johndoe",
                    email = "john@example.com",
                    phone = "123-456-7890",
                    website = "johndoe.com",
                    avatar = "avatar.jpg",
                    address = AddressModel("City", LocationModel("0", "0"), "Street", "Suite", "12345"),
                    company = CompanyModel("BS", "Catch phrase", "Company"),
                )
            val getUserByIdUseCase = GetUserByIdUseCase { Result.success(expectedUser) }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // When
            viewModel.handleEvent(DetailContract.Event.GetUserById(1))
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)

            val user = finalState.user
            assertEquals("ID should match", 1, user?.id)
            assertEquals("Name should match", "John Doe", user?.name)
            assertEquals("Username should match", "johndoe", user?.username)
            assertEquals("Email should match", "john@example.com", user?.email)
            assertEquals("Phone should match", "123-456-7890", user?.phone)
            assertEquals("Website should match", "johndoe.com", user?.website)
            assertEquals("Avatar should match", "avatar.jpg", user?.avatar)
        }

    @Test
    fun `loading state is set when fetching user`() =
        runTest {
            // Given
            val getUserByIdUseCase =
                GetUserByIdUseCase {
                    kotlinx.coroutines.delay(50)
                    Result.success(null)
                }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // When
            viewModel.handleEvent(DetailContract.Event.GetUserById(1))

            // Advance just a little to check loading state
            testDispatcher.scheduler.advanceTimeBy(10)

            // Then
            val loadingState = viewModel.uiState.value
            assertTrue("Should be loading during fetch", loadingState.isLoading)
            assertFalse("Should not have error during loading", loadingState.isError)
            assertNull("User should be null during loading", loadingState.user)
        }

    @Test
    fun `user not found returns null user`() =
        runTest {
            // Given
            val getUserByIdUseCase = GetUserByIdUseCase { Result.success(null) }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // When
            viewModel.handleEvent(DetailContract.Event.GetUserById(999))
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)
            assertNull("User should be null when not found", finalState.user)
        }

    @Test
    fun `network error shows error state`() =
        runTest {
            // Given
            val getUserByIdUseCase =
                GetUserByIdUseCase {
                    Result.failure(RuntimeException("Network error"))
                }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // When
            viewModel.handleEvent(DetailContract.Event.GetUserById(1))
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after error", finalState.isLoading)
            assertTrue("Error should be true on failure", finalState.isError)
            assertNull("User should be null on error", finalState.user)
        }

    @Test
    fun `multiple user requests reset previous states`() =
        runTest {
            // Given
            val getUserByIdUseCase =
                GetUserByIdUseCase { userId ->
                    when (userId) {
                        1 -> Result.failure(Exception("User 1 not found"))
                        2 ->
                            Result.success(
                                UserModel(
                                    id = 2,
                                    name = "Jane Doe",
                                    username = "janedoe",
                                    email = "jane@example.com",
                                    phone = "987-654-3210",
                                    website = "janedoe.com",
                                    avatar = "jane.jpg",
                                    address =
                                        AddressModel(
                                            "City2",
                                            LocationModel("1", "1"),
                                            "Street2",
                                            "Suite2",
                                            "54321",
                                        ),
                                    company = CompanyModel("BS2", "Phrase2", "Company2"),
                                ),
                            )
                        else -> Result.success(null)
                    }
                }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // First request - error
            viewModel.handleEvent(DetailContract.Event.GetUserById(1))
            advanceUntilIdle()

            val firstState = viewModel.uiState.value
            assertTrue("First request should show error", firstState.isError)
            assertNull("First request should have null user", firstState.user)

            // Second request - success (should reset previous error)
            viewModel.handleEvent(DetailContract.Event.GetUserById(2))
            advanceUntilIdle()

            val secondState = viewModel.uiState.value
            assertFalse("Second request should not show error", secondState.isError)
            assertEquals("Second request should have correct user", "Jane Doe", secondState.user?.name)
            assertFalse("Should not be loading after completion", secondState.isLoading)
        }

    @Test
    fun `user with complete data loads correctly`() =
        runTest {
            // Given
            val completeUser =
                UserModel(
                    id = 5,
                    name = "Complete User",
                    username = "complete",
                    email = "complete@test.com",
                    phone = "555-1234",
                    website = "complete.com",
                    avatar = "complete.jpg",
                    address =
                        AddressModel(
                            city = "Test City",
                            geo = LocationModel("40.7128", "-74.0060"),
                            street = "123 Test St",
                            suite = "Apt 4B",
                            zipcode = "10001",
                        ),
                    company =
                        CompanyModel(
                            bs = "Test BS",
                            catchPhrase = "Test Catch Phrase",
                            name = "Test Company",
                        ),
                )
            val getUserByIdUseCase = GetUserByIdUseCase { Result.success(completeUser) }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // When
            viewModel.handleEvent(DetailContract.Event.GetUserById(5))
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)

            val user = finalState.user
            assertEquals("ID should match", 5, user?.id)
            assertEquals("Name should match", "Complete User", user?.name)
            assertEquals("Username should match", "complete", user?.username)
            assertEquals("Email should match", "complete@test.com", user?.email)
            assertEquals("Phone should match", "555-1234", user?.phone)
            assertEquals("Website should match", "complete.com", user?.website)
            assertEquals("Avatar should match", "complete.jpg", user?.avatar)
        }

    @Test
    fun `invalid user id shows error`() =
        runTest {
            // Given
            val getUserByIdUseCase =
                GetUserByIdUseCase { userId ->
                    if (userId <= 0) {
                        Result.failure(IllegalArgumentException("Invalid user ID"))
                    } else {
                        Result.success(null)
                    }
                }
            val viewModel = DetailViewModel(getUserByIdUseCase)

            // When
            viewModel.handleEvent(DetailContract.Event.GetUserById(-1))
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after error", finalState.isLoading)
            assertTrue("Error should be true on invalid ID", finalState.isError)
            assertNull("User should be null on error", finalState.user)
        }
}
