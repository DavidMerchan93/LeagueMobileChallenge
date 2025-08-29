package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.model.AddressModel
import com.davidmerchan.domain.model.CompanyModel
import com.davidmerchan.domain.model.LocationModel
import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPostsWithUsersUseCaseTest {

    private val postRepository: PostRepository = mockk()
    private val userRepository: UserRepository = mockk()

    @Test
    fun `getPostsWithUsers returns combined data when both repositories succeed`() = runTest {
        // Given
        val posts = listOf(
            PostModel(1, 1, "Post 1", "Body 1"),
            PostModel(2, 2, "Post 2", "Body 2")
        )
        val users = listOf(
            UserModel(1, "User 1", createAddress(), "avatar1.jpg", createCompany(), "user1@test.com", "123", "user1", "site1.com"),
            UserModel(2, "User 2", createAddress(), "avatar2.jpg", createCompany(), "user2@test.com", "456", "user2", "site2.com")
        )

        coEvery { postRepository.getPosts() } returns Result.success(posts)
        coEvery { userRepository.getUsers() } returns Result.success(users)

        // When
        val result = getPostsWithUsers(postRepository, userRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val postsWithUsers = result.getOrNull()!!
        assertEquals("Should have 2 posts", 2, postsWithUsers.size)
        
        val firstPost = postsWithUsers[0]
        assertEquals("Post ID should match", 1, firstPost.id)
        assertEquals("Post title should match", "Post 1", firstPost.title)
        assertEquals("Post description should match", "Body 1", firstPost.description)
        assertEquals("User name should match", "User 1", firstPost.userName)
        assertEquals("Avatar should match", "avatar1.jpg", firstPost.avatar)
    }

    @Test
    fun `getPostsWithUsers returns posts without user data when user repository fails`() = runTest {
        // Given
        val posts = listOf(
            PostModel(1, 1, "Post 1", "Body 1"),
            PostModel(2, 2, "Post 2", "Body 2")
        )

        coEvery { postRepository.getPosts() } returns Result.success(posts)
        coEvery { userRepository.getUsers() } returns Result.failure(Exception("User fetch failed"))

        // When
        val result = getPostsWithUsers(postRepository, userRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val postsWithUsers = result.getOrNull()!!
        assertEquals("Should have 2 posts", 2, postsWithUsers.size)
        
        val firstPost = postsWithUsers[0]
        assertEquals("Post ID should match", 1, firstPost.id)
        assertEquals("Post title should match", "Post 1", firstPost.title)
        assertEquals("User name should be empty", "", firstPost.userName)
        assertEquals("Avatar should be empty", "", firstPost.avatar)
    }

    @Test
    fun `getPostsWithUsers returns empty list when post repository fails`() = runTest {
        // Given
        val users = listOf(
            UserModel(1, "User 1", createAddress(), "avatar1.jpg", createCompany(), "user1@test.com", "123", "user1", "site1.com")
        )

        coEvery { postRepository.getPosts() } returns Result.failure(Exception("Post fetch failed"))
        coEvery { userRepository.getUsers() } returns Result.success(users)

        // When
        val result = getPostsWithUsers(postRepository, userRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val postsWithUsers = result.getOrNull()!!
        assertTrue("Should return empty list", postsWithUsers.isEmpty())
    }

    @Test
    fun `getPostsWithUsers returns empty list when both repositories fail`() = runTest {
        // Given
        coEvery { postRepository.getPosts() } returns Result.failure(Exception("Post fetch failed"))
        coEvery { userRepository.getUsers() } returns Result.failure(Exception("User fetch failed"))

        // When
        val result = getPostsWithUsers(postRepository, userRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val postsWithUsers = result.getOrNull()!!
        assertTrue("Should return empty list", postsWithUsers.isEmpty())
    }

    @Test
    fun `getPostsWithUsers handles posts without matching users`() = runTest {
        // Given
        val posts = listOf(
            PostModel(1, 1, "Post 1", "Body 1"),
            PostModel(2, 999, "Post 2", "Body 2") // User 999 doesn't exist
        )
        val users = listOf(
            UserModel(1, "User 1", createAddress(), "avatar1.jpg", createCompany(), "user1@test.com", "123", "user1", "site1.com")
        )

        coEvery { postRepository.getPosts() } returns Result.success(posts)
        coEvery { userRepository.getUsers() } returns Result.success(users)

        // When
        val result = getPostsWithUsers(postRepository, userRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val postsWithUsers = result.getOrNull()!!
        assertEquals("Should have 2 posts", 2, postsWithUsers.size)
        
        // First post should have user data
        assertEquals("First post should have user name", "User 1", postsWithUsers[0].userName)
        assertEquals("First post should have avatar", "avatar1.jpg", postsWithUsers[0].avatar)
        
        // Second post should have empty user data
        assertEquals("Second post should have empty user name", "", postsWithUsers[1].userName)
        assertEquals("Second post should have empty avatar", "", postsWithUsers[1].avatar)
    }

    @Test
    fun `getPostsWithUsers handles empty posts list`() = runTest {
        // Given
        val users = listOf(
            UserModel(1, "User 1", createAddress(), "avatar1.jpg", createCompany(), "user1@test.com", "123", "user1", "site1.com")
        )

        coEvery { postRepository.getPosts() } returns Result.success(emptyList())
        coEvery { userRepository.getUsers() } returns Result.success(users)

        // When
        val result = getPostsWithUsers(postRepository, userRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val postsWithUsers = result.getOrNull()!!
        assertTrue("Should return empty list", postsWithUsers.isEmpty())
    }

    @Test
    fun `getPostsWithUsers handles empty users list`() = runTest {
        // Given
        val posts = listOf(
            PostModel(1, 1, "Post 1", "Body 1")
        )

        coEvery { postRepository.getPosts() } returns Result.success(posts)
        coEvery { userRepository.getUsers() } returns Result.success(emptyList())

        // When
        val result = getPostsWithUsers(postRepository, userRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val postsWithUsers = result.getOrNull()!!
        assertEquals("Should have 1 post", 1, postsWithUsers.size)
        assertEquals("User name should be empty", "", postsWithUsers[0].userName)
        assertEquals("Avatar should be empty", "", postsWithUsers[0].avatar)
    }

    @Test
    fun `functional interface works correctly`() = runTest {
        // Given
        val useCase = GetPostsWithUsersUseCase { 
            Result.success(listOf(
                com.davidmerchan.domain.model.PostWithUserModel(1, 1, "Title", "Body", "avatar.jpg", "User")
            ))
        }

        // When
        val result = useCase()

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should have 1 post", 1, result.getOrNull()!!.size)
    }

    private fun createAddress() = AddressModel(
        city = "Test City",
        geo = LocationModel("0", "0"),
        street = "Test Street",
        suite = "Test Suite",
        zipcode = "12345"
    )

    private fun createCompany() = CompanyModel(
        bs = "Test BS",
        catchPhrase = "Test Phrase",
        name = "Test Company"
    )
}