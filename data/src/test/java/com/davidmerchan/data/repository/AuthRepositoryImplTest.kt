package com.davidmerchan.data.repository

import com.davidmerchan.domain.repository.AuthRepository
import com.davidmerchan.network.api.AuthApi
import com.davidmerchan.network.dto.AuthDto
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private val authApi: AuthApi = mockk()
    private lateinit var authRepository: AuthRepository

    @Before
    fun setup() {
        authRepository = AuthRepositoryImpl(authApi)
    }

    @After
    fun tearDown() {
        clearMocks(authApi)
    }

    @Test
    fun `login success returns api key`() = runTest {
        // Given
        val username = "testuser"
        val password = "testpass"
        val expectedApiKey = "test-api-key-123"
        val authDto = AuthDto(apiKey = expectedApiKey)

        coEvery {
            authApi.login(any())
        } returns authDto

        // When
        val result = authRepository.login(username, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedApiKey, result.getOrNull())
    }

    @Test
    fun `login network error returns failure`() = runTest {
        // Given
        val username = "testuser"
        val password = "testpass"
        val exception = RuntimeException("Network error")

        coEvery {
            authApi.login(any())
        } throws exception

        // When
        val result = authRepository.login(username, password)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `login with empty credentials handles correctly`() = runTest {
        // Given
        val username = ""
        val password = ""
        val expectedApiKey = "empty-creds-key"
        val authDto = AuthDto(apiKey = expectedApiKey)

        coEvery {
            authApi.login(any())
        } returns authDto

        // When
        val result = authRepository.login(username, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedApiKey, result.getOrNull())
    }

    @Test
    fun `login with null api key returns empty string`() = runTest {
        // Given
        val username = "testuser"
        val password = "testpass"
        val authDto = AuthDto(apiKey = "")

        coEvery {
            authApi.login(any())
        } returns authDto

        // When
        val result = authRepository.login(username, password)

        // Then
        assertTrue(result.isSuccess)
        assertEquals("", result.getOrNull())
    }
}
