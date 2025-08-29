package com.davidmerchan.data.repository

import com.davidmerchan.database.storage.Storage
import com.davidmerchan.database.storage.StorageConstants
import com.davidmerchan.domain.repository.TokenRepository
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TokenRepositoryImplTest {
    private val storage: Storage = mockk()

    private lateinit var tokenRepository: TokenRepository

    @Before
    fun setup() {
        tokenRepository = TokenRepositoryImpl(storage)
    }

    @After
    fun tearDown() {
        clearMocks(storage)
    }

    @Test
    fun `getToken returns token successfully from storage`() =
        runTest {
            // Given
            val expectedToken = "test-token-123"
            coEvery {
                storage.readSecureString(StorageConstants.API_KEY)
            } returns flowOf(expectedToken)

            // When
            val result = tokenRepository.getToken()

            // Then
            assertTrue(result.isSuccess)
            assertEquals(expectedToken, result.getOrNull())
            coVerify { storage.readSecureString(StorageConstants.API_KEY) }
        }

    @Test
    fun `getToken returns null when no token stored`() =
        runTest {
            // Given
            coEvery {
                storage.readSecureString(StorageConstants.API_KEY)
            } returns flowOf(null)

            // When
            val result = tokenRepository.getToken()

            // Then
            assertTrue(result.isSuccess)
            assertNull(result.getOrNull())
            coVerify { storage.readSecureString(StorageConstants.API_KEY) }
        }

    @Test
    fun `getToken handles storage error gracefully`() =
        runTest {
            // Given
            val exception = RuntimeException("Storage error")
            coEvery {
                storage.readSecureString(StorageConstants.API_KEY)
            } throws exception

            // When
            val result = tokenRepository.getToken()

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify { storage.readSecureString(StorageConstants.API_KEY) }
        }

    @Test
    fun `saveToken saves token successfully`() =
        runTest {
            // Given
            val token = "new-token-456"
            coEvery {
                storage.saveSecureString(StorageConstants.API_KEY, token)
            } returns Unit

            // When
            val result = tokenRepository.saveToken(token)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(true, result.getOrNull())
            coVerify { storage.saveSecureString(StorageConstants.API_KEY, token) }
        }

    @Test
    fun `saveToken handles storage error gracefully`() =
        runTest {
            // Given
            val token = "new-token-456"
            val exception = RuntimeException("Storage save error")
            coEvery {
                storage.saveSecureString(StorageConstants.API_KEY, token)
            } throws exception

            // When
            val result = tokenRepository.saveToken(token)

            // Then
            assertTrue(result.isFailure)
            assertEquals(exception, result.exceptionOrNull())
            coVerify { storage.saveSecureString(StorageConstants.API_KEY, token) }
        }

    @Test
    fun `saveToken handles empty token correctly`() =
        runTest {
            // Given
            val emptyToken = ""
            coEvery {
                storage.saveSecureString(StorageConstants.API_KEY, emptyToken)
            } returns Unit

            // When
            val result = tokenRepository.saveToken(emptyToken)

            // Then
            assertTrue(result.isSuccess)
            assertEquals(true, result.getOrNull())
            coVerify { storage.saveSecureString(StorageConstants.API_KEY, emptyToken) }
        }
}
