package com.davidmerchan.data.repository

import com.davidmerchan.database.dao.PostDao
import com.davidmerchan.database.entities.PostEntity
import com.davidmerchan.database.storage.Storage
import com.davidmerchan.domain.model.PostModel
import com.davidmerchan.domain.repository.PostRepository
import com.davidmerchan.network.api.PostApi
import com.davidmerchan.network.dto.PostDto
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PostRepositoryImplTest {

    private val postApi: PostApi = mockk()
    private val storage: Storage = mockk()
    private val postDao: PostDao = mockk()

    private lateinit var postRepository: PostRepository

    @Before
    fun setup() {
        postRepository = PostRepositoryImpl(postApi, storage, postDao)
    }

    @After
    fun tearDown() {
        clearMocks(postApi, storage, postDao)
    }

    @Test
    fun `getPosts returns cached data when database has posts`() = runTest {
        // Given
        val cachedPosts = listOf(
            PostEntity(id = 1, userId = 1, title = "Cached Post", body = "Cached content")
        )
        val expectedPosts = listOf(
            PostModel(id = 1, userId = 1, title = "Cached Post", body = "Cached content")
        )

        coEvery { postDao.getAllPosts() } returns cachedPosts

        // When
        val result = postRepository.getPosts()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedPosts, result.getOrNull())
        coVerify(exactly = 0) {
            postApi.getPosts(any())
            storage.readSecureString(any())
        }
    }

    @Test
    fun `getPosts fetches from API when database is empty and saves to cache`() = runTest {
        // Given
        val accessToken = "test-token-123"
        val apiPosts = listOf(
            PostDto(id = 1, userId = 1, title = "API Post", body = "API content")
        )
        val expectedPosts = listOf(
            PostModel(id = 1, userId = 1, title = "API Post", body = "API content")
        )
        val expectedEntities = listOf(
            PostEntity(id = 1, userId = 1, title = "API Post", body = "API content")
        )

        coEvery { postDao.getAllPosts() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(accessToken)
        coEvery { postApi.getPosts(accessToken) } returns apiPosts
        coEvery { postDao.insertPosts(any()) } returns Unit

        // When
        val result = postRepository.getPosts()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedPosts, result.getOrNull())
        coVerify {
            postDao.getAllPosts()
            storage.readSecureString("league_API_KEY")
            postApi.getPosts(accessToken)
            postDao.insertPosts(expectedEntities)
        }
    }

    @Test
    fun `getPosts handles API error gracefully`() = runTest {
        // Given
        val accessToken = "test-token-123"
        val exception = RuntimeException("API Error")

        coEvery { postDao.getAllPosts() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(accessToken)
        coEvery { postApi.getPosts(accessToken) } throws exception

        // When
        val result = postRepository.getPosts()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())

        coVerify {
            postDao.getAllPosts()
            storage.readSecureString("league_API_KEY")
            postApi.getPosts(accessToken)
        }
        coVerify(exactly = 0) { postDao.insertPosts(any()) }
    }

    @Test
    fun `getPosts handles empty token gracefully`() = runTest {
        // Given
        val emptyToken = ""
        val apiPosts = listOf(
            PostDto(id = 1, userId = 1, title = "API Post", body = "API content")
        )
        val expectedPosts = listOf(
            PostModel(id = 1, userId = 1, title = "API Post", body = "API content")
        )

        coEvery { postDao.getAllPosts() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(emptyToken)
        coEvery { postApi.getPosts(emptyToken) } returns apiPosts
        coEvery { postDao.insertPosts(any()) } returns Unit

        // When
        val result = postRepository.getPosts()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedPosts, result.getOrNull())
        coVerify { postApi.getPosts(emptyToken) }
    }

    @Test
    fun `getPosts handles null token from storage`() = runTest {
        // Given
        val apiPosts = listOf(
            PostDto(id = 1, userId = 1, title = "API Post", body = "API content")
        )
        val expectedPosts = listOf(
            PostModel(id = 1, userId = 1, title = "API Post", body = "API content")
        )

        coEvery { postDao.getAllPosts() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(null)
        coEvery { postApi.getPosts("") } returns apiPosts
        coEvery { postDao.insertPosts(any()) } returns Unit

        // When
        val result = postRepository.getPosts()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedPosts, result.getOrNull())
        coVerify { postApi.getPosts("") }
    }

    @Test
    fun `getPosts handles database error when saving API data`() = runTest {
        // Given
        val accessToken = "test-token-123"
        val apiPosts = listOf(
            PostDto(id = 1, userId = 1, title = "API Post", body = "API content")
        )
        val dbException = RuntimeException("Database save error")

        coEvery { postDao.getAllPosts() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(accessToken)
        coEvery { postApi.getPosts(accessToken) } returns apiPosts
        coEvery { postDao.insertPosts(any()) } throws dbException

        // When
        val result = postRepository.getPosts()

        // Then
        assertTrue(result.isFailure)
        assertEquals(dbException, result.exceptionOrNull())
    }

}
