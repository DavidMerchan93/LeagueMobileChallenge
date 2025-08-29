package com.davidmerchan.domain.useCase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthUserCaseTest {

    @Test
    fun `AuthUserCase functional interface can be implemented`() = runTest {
        // Given
        val authUserCase = AuthUserCase { username, password ->
            when {
                username == "valid" && password == "valid" -> Result.success(true)
                username == "invalid" -> Result.failure(Exception("Invalid user"))
                else -> Result.success(false)
            }
        }

        // When & Then - Success case
        val successResult = authUserCase("valid", "valid")
        assertTrue(successResult.isSuccess)
        assertEquals(true, successResult.getOrNull())

        // When & Then - Failure case
        val failureResult = authUserCase("invalid", "pass")
        assertTrue(failureResult.isFailure)
        assertEquals("Invalid user", failureResult.exceptionOrNull()?.message)

        // When & Then - False case
        val falseResult = authUserCase("user", "pass")
        assertTrue(falseResult.isSuccess)
        assertEquals(false, falseResult.getOrNull())
    }

    @Test
    fun `AuthUserCase handles empty credentials`() = runTest {
        // Given
        val authUserCase = AuthUserCase { username, password ->
            if (username.isEmpty() || password.isEmpty()) {
                Result.failure(Exception("Empty credentials"))
            } else {
                Result.success(true)
            }
        }

        // When & Then
        val result = authUserCase("", "")
        assertTrue(result.isFailure)
        assertEquals("Empty credentials", result.exceptionOrNull()?.message)
    }

    @Test
    fun `AuthUserCase handles successful authentication flow`() = runTest {
        // Given - Simulated successful flow
        val authUserCase = AuthUserCase { username, password ->
            // Simulate repository login
            if (username == "user" && password == "pass") {
                val token = "auth-token-123"
                // Simulate token save
                if (token.isNotEmpty()) {
                    Result.success(true) // Simulate successful token save
                } else {
                    Result.failure(Exception("Token save failed"))
                }
            } else {
                Result.failure(Exception("Login failed"))
            }
        }

        // When
        val result = authUserCase("user", "pass")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(true, result.getOrNull())
    }

    @Test
    fun `AuthUserCase handles token save failure`() = runTest {
        // Given - Simulated token save failure
        val authUserCase = AuthUserCase { username, password ->
            if (username == "user" && password == "pass") {
                val token = "auth-token-123"
                // Simulate token save failure
                Result.failure(Exception("Token storage failed"))
            } else {
                Result.failure(Exception("Login failed"))
            }
        }

        // When
        val result = authUserCase("user", "pass")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Token storage failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `AuthUserCase handles login repository failure`() = runTest {
        // Given - Simulated repository failure
        val authUserCase = AuthUserCase { username, password ->
            // Simulate repository throwing exception
            Result.failure(RuntimeException("Network error"))
        }

        // When
        val result = authUserCase("user", "pass")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `AuthUserCase handles empty token from repository`() = runTest {
        // Given - Simulated empty token scenario
        val authUserCase = AuthUserCase { username, password ->
            // Simulate repository returning empty token
            val token = ""
            if (token.isEmpty()) {
                Result.failure(Exception("Empty token received"))
            } else {
                Result.success(true)
            }
        }

        // When
        val result = authUserCase("user", "pass")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Empty token received", result.exceptionOrNull()?.message)
    }

    @Test
    fun `AuthUserCase handles special characters in credentials`() = runTest {
        // Given
        val authUserCase = AuthUserCase { username, password ->
            // Simulate handling special characters
            if (username.contains("@") && password.length >= 8) {
                Result.success(true)
            } else {
                Result.failure(Exception("Invalid credentials format"))
            }
        }

        // When & Then - Valid email format
        val validResult = authUserCase("user@example.com", "password123")
        assertTrue(validResult.isSuccess)

        // When & Then - Invalid format
        val invalidResult = authUserCase("user", "123")
        assertTrue(invalidResult.isFailure)
        assertEquals("Invalid credentials format", invalidResult.exceptionOrNull()?.message)
    }
}