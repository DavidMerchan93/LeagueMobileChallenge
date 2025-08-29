package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.repository.TokenRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetAccessTokenUseCaseTest {

    private val tokenRepository: TokenRepository = mockk()

    @Test
    fun `getAccessToken returns valid token when repository succeeds`() = runTest {
        // Given
        val expectedToken = "valid_token_123"
        coEvery { tokenRepository.getToken() } returns Result.success(expectedToken)

        // When
        val result = getAccessToken(tokenRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Token should match", expectedToken, result.getOrNull())
    }

    @Test
    fun `getAccessToken returns null when repository returns null`() = runTest {
        // Given
        coEvery { tokenRepository.getToken() } returns Result.success(null)

        // When
        val result = getAccessToken(tokenRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertNull("Token should be null", result.getOrNull())
    }

    @Test
    fun `getAccessToken returns empty string when repository returns empty`() = runTest {
        // Given
        coEvery { tokenRepository.getToken() } returns Result.success("")

        // When
        val result = getAccessToken(tokenRepository)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Token should be empty", "", result.getOrNull())
    }

    @Test
    fun `getAccessToken returns failure when repository fails`() = runTest {
        // Given
        val expectedException = RuntimeException("Storage error")
        coEvery { tokenRepository.getToken() } returns Result.failure(expectedException)

        // When
        val result = getAccessToken(tokenRepository)

        // Then
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Exception should match", expectedException, result.exceptionOrNull())
    }

    @Test
    fun `functional interface works with valid token`() = runTest {
        // Given
        val useCase = GetAccessTokenUseCase { Result.success("functional_token") }

        // When
        val result = useCase()

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Token should match", "functional_token", result.getOrNull())
    }

    @Test
    fun `functional interface works with error`() = runTest {
        // Given
        val useCase = GetAccessTokenUseCase { Result.failure(Exception("Error")) }

        // When
        val result = useCase()

        // Then
        assertTrue("Result should be failure", result.isFailure)
    }
}