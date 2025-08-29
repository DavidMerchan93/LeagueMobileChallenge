package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.repository.TokenRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveTokenUseCaseTest {
    private val tokenRepository: TokenRepository = mockk()

    @Test
    fun `saveToken returns true when repository succeeds`() =
        runTest {
            // Given
            val token = "valid_token_123"
            coEvery { tokenRepository.saveToken(token) } returns Result.success(true)

            // When
            val result = saveToken(token, tokenRepository)

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertTrue("Should return true", result.getOrNull() == true)
        }

    @Test
    fun `saveToken returns false when repository returns false`() =
        runTest {
            // Given
            val token = "some_token"
            coEvery { tokenRepository.saveToken(token) } returns Result.success(false)

            // When
            val result = saveToken(token, tokenRepository)

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertFalse("Should return false", result.getOrNull() == true)
        }

    @Test
    fun `saveToken returns failure when repository fails`() =
        runTest {
            // Given
            val token = "token"
            val expectedException = RuntimeException("Storage error")
            coEvery { tokenRepository.saveToken(token) } returns Result.failure(expectedException)

            // When
            val result = saveToken(token, tokenRepository)

            // Then
            assertTrue("Result should be failure", result.isFailure)
            assertEquals("Exception should match", expectedException, result.exceptionOrNull())
        }

    @Test
    fun `saveToken handles empty token`() =
        runTest {
            // Given
            val emptyToken = ""
            coEvery { tokenRepository.saveToken(emptyToken) } returns Result.success(true)

            // When
            val result = saveToken(emptyToken, tokenRepository)

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertTrue("Should save empty token successfully", result.getOrNull() == true)
        }

    @Test
    fun `saveToken handles very long token`() =
        runTest {
            // Given
            val longToken = "a".repeat(1000)
            coEvery { tokenRepository.saveToken(longToken) } returns Result.success(true)

            // When
            val result = saveToken(longToken, tokenRepository)

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertTrue("Should save long token successfully", result.getOrNull() == true)
        }

    @Test
    fun `saveToken handles special characters in token`() =
        runTest {
            // Given
            val specialToken = "token!@#$%^&*()_+-=[]{}|;:,.<>?"
            coEvery { tokenRepository.saveToken(specialToken) } returns Result.success(true)

            // When
            val result = saveToken(specialToken, tokenRepository)

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertTrue("Should save token with special chars", result.getOrNull() == true)
        }

    @Test
    fun `saveToken handles storage full error`() =
        runTest {
            // Given
            val token = "token"
            val storageException = Exception("Storage full")
            coEvery { tokenRepository.saveToken(token) } returns Result.failure(storageException)

            // When
            val result = saveToken(token, tokenRepository)

            // Then
            assertTrue("Result should be failure", result.isFailure)
            assertEquals("Exception should match", storageException, result.exceptionOrNull())
        }

    @Test
    fun `saveToken handles encryption error`() =
        runTest {
            // Given
            val token = "token"
            val encryptionException = javax.crypto.BadPaddingException("Encryption failed")
            coEvery { tokenRepository.saveToken(token) } returns Result.failure(encryptionException)

            // When
            val result = saveToken(token, tokenRepository)

            // Then
            assertTrue("Result should be failure", result.isFailure)
            assertTrue("Should be encryption exception", result.exceptionOrNull() is javax.crypto.BadPaddingException)
        }

    @Test
    fun `functional interface works with successful save`() =
        runTest {
            // Given
            val useCase =
                SaveTokenUseCase { token ->
                    if (token.isNotEmpty()) Result.success(true) else Result.success(false)
                }

            // When
            val result = useCase("valid_token")

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertTrue("Should return true", result.getOrNull() == true)
        }

    @Test
    fun `functional interface works with empty token`() =
        runTest {
            // Given
            val useCase =
                SaveTokenUseCase { token ->
                    if (token.isNotEmpty()) Result.success(true) else Result.success(false)
                }

            // When
            val result = useCase("")

            // Then
            assertTrue("Result should be success", result.isSuccess)
            assertFalse("Should return false for empty token", result.getOrNull() == true)
        }

    @Test
    fun `functional interface works with error`() =
        runTest {
            // Given
            val useCase = SaveTokenUseCase { Result.failure(Exception("Save error")) }

            // When
            val result = useCase("token")

            // Then
            assertTrue("Result should be failure", result.isFailure)
        }
}
