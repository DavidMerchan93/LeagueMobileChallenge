package com.davidmerchan.presentation.feature.home

import com.davidmerchan.domain.model.PostWithUserModel
import com.davidmerchan.domain.useCase.GetPostsWithUsersUseCase
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
class HomeViewModelTest {
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
    fun `initial state starts loading and loads posts automatically`() =
        runTest {
            // Given
            val expectedPosts =
                listOf(
                    PostWithUserModel(1, 1, "Title 1", "Body 1", "avatar1.jpg", "User 1"),
                    PostWithUserModel(2, 2, "Title 2", "Body 2", "avatar2.jpg", "User 2"),
                )
            val getPostsUseCase = GetPostsWithUsersUseCase { Result.success(expectedPosts) }

            // When
            val viewModel = HomeViewModel(getPostsUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)
            assertEquals("Should have correct number of items", 2, finalState.items.size)
            assertEquals("First item title should match", "Title 1", finalState.items[0].title)
            assertEquals("Second item title should match", "Title 2", finalState.items[1].title)
        }

    @Test
    fun `loading state is set initially`() =
        runTest {
            // Given
            val getPostsUseCase =
                GetPostsWithUsersUseCase {
                    kotlinx.coroutines.delay(50)
                    Result.success(emptyList())
                }

            // When
            val viewModel = HomeViewModel(getPostsUseCase)

            // Advance just a little to check loading state
            testDispatcher.scheduler.advanceTimeBy(10)

            // Then
            val loadingState = viewModel.uiState.value
            assertTrue("Should be loading initially", loadingState.isLoading)
            assertFalse("Should not have error during loading", loadingState.isError)
            assertTrue("Items should be empty during loading", loadingState.items.isEmpty())
        }

    @Test
    fun `successful posts loading updates state correctly`() =
        runTest {
            // Given
            val expectedPosts =
                listOf(
                    PostWithUserModel(1, 1, "Post Title", "Post Body", "user-avatar.jpg", "John Doe"),
                )
            val getPostsUseCase = GetPostsWithUsersUseCase { Result.success(expectedPosts) }

            // When
            val viewModel = HomeViewModel(getPostsUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)
            assertEquals("Should have one item", 1, finalState.items.size)

            val item = finalState.items[0]
            assertEquals("ID should match", 1, item.id)
            assertEquals("Title should match", "Post Title", item.title)
            assertEquals("Description should match", "Post Body", item.description)
            assertEquals("Avatar should match", "user-avatar.jpg", item.avatar)
            assertEquals("User name should match", "John Doe", item.userName)
            assertEquals("User ID should match", 1, item.userId)
        }

    @Test
    fun `network error shows error state`() =
        runTest {
            // Given
            val getPostsUseCase =
                GetPostsWithUsersUseCase {
                    Result.failure(RuntimeException("Network error"))
                }

            // When
            val viewModel = HomeViewModel(getPostsUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after error", finalState.isLoading)
            assertTrue("Error should be true on failure", finalState.isError)
            assertTrue("Items should be empty on error", finalState.items.isEmpty())
        }

    @Test
    fun `empty posts list loads successfully`() =
        runTest {
            // Given
            val getPostsUseCase = GetPostsWithUsersUseCase { Result.success(emptyList()) }

            // When
            val viewModel = HomeViewModel(getPostsUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)
            assertTrue("Items should be empty", finalState.items.isEmpty())
        }

    @Test
    fun `large posts list loads correctly`() =
        runTest {
            // Given
            val largePosts =
                (1..100).map { id ->
                    PostWithUserModel(
                        id = id,
                        userId = id,
                        title = "Title $id",
                        description = "Description $id",
                        avatar = "avatar$id.jpg",
                        userName = "User $id",
                    )
                }
            val getPostsUseCase = GetPostsWithUsersUseCase { Result.success(largePosts) }

            // When
            val viewModel = HomeViewModel(getPostsUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)
            assertEquals("Should have correct number of items", 100, finalState.items.size)
            assertEquals("First item should be correct", "Title 1", finalState.items[0].title)
            assertEquals("Last item should be correct", "Title 100", finalState.items[99].title)
        }

    @Test
    fun `posts without user data are handled correctly`() =
        runTest {
            // Given
            val postsWithoutUsers =
                listOf(
                    PostWithUserModel(1, 1, "Title", "Body", "", ""),
                    PostWithUserModel(2, 2, "Title 2", "Body 2", "", ""),
                )
            val getPostsUseCase = GetPostsWithUsersUseCase { Result.success(postsWithoutUsers) }

            // When
            val viewModel = HomeViewModel(getPostsUseCase)
            advanceUntilIdle()

            // Then
            val finalState = viewModel.uiState.value
            assertFalse("Loading should be false after completion", finalState.isLoading)
            assertFalse("Error should be false on success", finalState.isError)
            assertEquals("Should have correct number of items", 2, finalState.items.size)
            assertEquals("Avatar should be empty", "", finalState.items[0].avatar)
            assertEquals("User name should be empty", "", finalState.items[0].userName)
        }
}
