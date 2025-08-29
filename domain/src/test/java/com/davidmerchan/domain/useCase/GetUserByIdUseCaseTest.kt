package com.davidmerchan.domain.useCase

import com.davidmerchan.domain.model.AddressModel
import com.davidmerchan.domain.model.CompanyModel
import com.davidmerchan.domain.model.LocationModel
import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUserByIdUseCaseTest {

    private val userRepository: UserRepository = mockk()

    @Test
    fun `getUserById returns user when repository succeeds`() = runTest {
        // Given
        val expectedUser = UserModel(
            id = 1,
            name = "John Doe",
            address = createAddress(),
            avatar = "john.jpg",
            company = createCompany(),
            email = "john@test.com",
            phone = "123-456-7890",
            username = "johndoe",
            website = "johndoe.com"
        )
        coEvery { userRepository.getUserById(1) } returns Result.success(expectedUser)

        // When
        val result = getUserById(userRepository, 1)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        val user = result.getOrNull()
        assertEquals("User ID should match", 1, user?.id)
        assertEquals("User name should match", "John Doe", user?.name)
        assertEquals("Username should match", "johndoe", user?.username)
        assertEquals("Email should match", "john@test.com", user?.email)
        assertEquals("Phone should match", "123-456-7890", user?.phone)
        assertEquals("Website should match", "johndoe.com", user?.website)
        assertEquals("Avatar should match", "john.jpg", user?.avatar)
    }

    @Test
    fun `getUserById returns null when user not found`() = runTest {
        // Given
        coEvery { userRepository.getUserById(999) } returns Result.success(null)

        // When
        val result = getUserById(userRepository, 999)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertNull("User should be null", result.getOrNull())
    }

    @Test
    fun `getUserById returns failure when repository fails`() = runTest {
        // Given
        val expectedException = RuntimeException("Database error")
        coEvery { userRepository.getUserById(1) } returns Result.failure(expectedException)

        // When
        val result = getUserById(userRepository, 1)

        // Then
        assertTrue("Result should be failure", result.isFailure)
        assertEquals("Exception should match", expectedException, result.exceptionOrNull())
    }

    @Test
    fun `getUserById handles different user IDs correctly`() = runTest {
        // Given
        val user5 = UserModel(5, "User 5", createAddress(), "user5.jpg", createCompany(), "user5@test.com", "555-0005", "user5", "user5.com")
        val user10 = UserModel(10, "User 10", createAddress(), "user10.jpg", createCompany(), "user10@test.com", "555-0010", "user10", "user10.com")

        coEvery { userRepository.getUserById(5) } returns Result.success(user5)
        coEvery { userRepository.getUserById(10) } returns Result.success(user10)

        // When
        val result5 = getUserById(userRepository, 5)
        val result10 = getUserById(userRepository, 10)

        // Then
        assertTrue("Result 5 should be success", result5.isSuccess)
        assertTrue("Result 10 should be success", result10.isSuccess)
        assertEquals("User 5 name should match", "User 5", result5.getOrNull()?.name)
        assertEquals("User 10 name should match", "User 10", result10.getOrNull()?.name)
    }

    @Test
    fun `getUserById handles network timeout error`() = runTest {
        // Given
        val timeoutException = java.net.SocketTimeoutException("Timeout")
        coEvery { userRepository.getUserById(1) } returns Result.failure(timeoutException)

        // When
        val result = getUserById(userRepository, 1)

        // Then
        assertTrue("Result should be failure", result.isFailure)
        assertTrue("Should be timeout exception", result.exceptionOrNull() is java.net.SocketTimeoutException)
    }

    @Test
    fun `functional interface works with valid user`() = runTest {
        // Given
        val expectedUser = UserModel(1, "Test User", createAddress(), "test.jpg", createCompany(), "test@test.com", "123", "testuser", "test.com")
        val useCase = GetUserByIdUseCase { userId ->
            if (userId == 1) Result.success(expectedUser) else Result.success(null)
        }

        // When
        val result = useCase(1)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("User name should match", "Test User", result.getOrNull()?.name)
    }

    @Test
    fun `functional interface works with user not found`() = runTest {
        // Given
        val useCase = GetUserByIdUseCase { Result.success(null) }

        // When
        val result = useCase(999)

        // Then
        assertTrue("Result should be success", result.isSuccess)
        assertNull("User should be null", result.getOrNull())
    }

    @Test
    fun `functional interface works with error`() = runTest {
        // Given
        val useCase = GetUserByIdUseCase { Result.failure(Exception("Error")) }

        // When
        val result = useCase(1)

        // Then
        assertTrue("Result should be failure", result.isFailure)
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