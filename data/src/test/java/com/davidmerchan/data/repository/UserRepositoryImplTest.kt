package com.davidmerchan.data.repository

import com.davidmerchan.database.dao.UserDao
import com.davidmerchan.database.entities.UserEntity
import com.davidmerchan.database.storage.Storage
import com.davidmerchan.database.storage.StorageConstants
import com.davidmerchan.domain.model.AddressModel
import com.davidmerchan.domain.model.CompanyModel
import com.davidmerchan.domain.model.LocationModel
import com.davidmerchan.domain.model.UserModel
import com.davidmerchan.domain.repository.UserRepository
import com.davidmerchan.network.api.UserApi
import com.davidmerchan.network.dto.AddressDto
import com.davidmerchan.network.dto.CompanyDto
import com.davidmerchan.network.dto.LocationDto
import com.davidmerchan.network.dto.UserDto
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

class UserRepositoryImplTest {

    private val userApi: UserApi = mockk()
    private val storage: Storage = mockk()
    private val userDao: UserDao = mockk()

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userRepository = UserRepositoryImpl(userApi, storage, userDao)
    }

    @After
    fun tearDown() {
        clearMocks(userApi, storage, userDao)
    }

    private fun createSampleUserEntity() = UserEntity(
        id = 1,
        name = "John Doe",
        city = "New York",
        avatar = "avatar.jpg",
        email = "john@example.com",
        phone = "123-456-7890",
        userName = "johndoe",
        website = "johndoe.com",
        lat = "40.7128",
        lng = "-74.0060",
        street = "123 Main St",
        suite = "Apt 1",
        zipCode = "10001",
        bs = "Business",
        catchPhrase = "Catch phrase",
        companyName = "Company Inc"
    )

    private fun createSampleUserModel() = UserModel(
        id = 1,
        name = "John Doe",
        address = AddressModel(
            city = "New York",
            street = "123 Main St",
            suite = "Apt 1",
            zipcode = "10001",
            geo = LocationModel(lat = "40.7128", lng = "-74.0060")
        ),
        avatar = "avatar.jpg",
        company = CompanyModel(
            bs = "Business",
            catchPhrase = "Catch phrase",
            name = "Company Inc"
        ),
        email = "john@example.com",
        phone = "123-456-7890",
        username = "johndoe",
        website = "johndoe.com"
    )

    private fun createSampleUserDto() = UserDto(
        id = 1,
        name = "John Doe",
        address = AddressDto(
            city = "New York",
            street = "123 Main St",
            suite = "Apt 1",
            zipcode = "10001",
            geo = LocationDto(lat = "40.7128", lng = "-74.0060")
        ),
        avatar = "avatar.jpg",
        company = CompanyDto(
            bs = "Business",
            catchPhrase = "Catch phrase",
            name = "Company Inc"
        ),
        email = "john@example.com",
        phone = "123-456-7890",
        username = "johndoe",
        website = "johndoe.com"
    )

    @Test
    fun `getUsers returns cached data when database has users`() = runTest {
        // Given
        val cachedUsers = listOf(createSampleUserEntity())
        val expectedUsers = listOf(createSampleUserModel())

        coEvery { userDao.getAllUsers() } returns cachedUsers

        // When
        val result = userRepository.getUsers()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
        coVerify(exactly = 0) {
            userApi.getUsers(any())
            storage.readSecureString(any())
        }
    }

    @Test
    fun `getUsers fetches from API when database is empty and saves to cache`() = runTest {
        // Given
        val accessToken = "test-token-123"
        val apiUsers = listOf(createSampleUserDto())
        val expectedUsers = listOf(createSampleUserModel())
        val expectedEntities = listOf(createSampleUserEntity())

        coEvery { userDao.getAllUsers() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(accessToken)
        coEvery { userApi.getUsers(accessToken) } returns apiUsers
        coEvery { userDao.insertUsers(any()) } returns Unit

        // When
        val result = userRepository.getUsers()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
        coVerify {
            userDao.getAllUsers()
            storage.readSecureString(StorageConstants.API_KEY)
            userApi.getUsers(accessToken)
            userDao.insertUsers(expectedEntities)
        }
    }

    @Test
    fun `getUsers handles API error gracefully`() = runTest {
        // Given
        val accessToken = "test-token-123"
        val exception = RuntimeException("API Error")

        coEvery { userDao.getAllUsers() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(accessToken)
        coEvery { userApi.getUsers(accessToken) } throws exception

        // When
        val result = userRepository.getUsers()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify {
            userDao.getAllUsers()
            storage.readSecureString(StorageConstants.API_KEY)
            userApi.getUsers(accessToken)
        }
        coVerify(exactly = 0) { userDao.insertUsers(any()) }
    }

    @Test
    fun `getUsers handles empty token gracefully`() = runTest {
        // Given
        val emptyToken = ""
        val apiUsers = listOf(createSampleUserDto())
        val expectedUsers = listOf(createSampleUserModel())

        coEvery { userDao.getAllUsers() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(emptyToken)
        coEvery { userApi.getUsers(emptyToken) } returns apiUsers
        coEvery { userDao.insertUsers(any()) } returns Unit

        // When
        val result = userRepository.getUsers()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
        coVerify { userApi.getUsers(emptyToken) }
    }

    @Test
    fun `getUsers handles null token from storage`() = runTest {
        // Given
        val apiUsers = listOf(createSampleUserDto())
        val expectedUsers = listOf(createSampleUserModel())

        coEvery { userDao.getAllUsers() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(null)
        coEvery { userApi.getUsers("") } returns apiUsers
        coEvery { userDao.insertUsers(any()) } returns Unit

        // When
        val result = userRepository.getUsers()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
        coVerify { userApi.getUsers("") }
    }

    @Test
    fun `getUsers handles database error when saving API data`() = runTest {
        // Given
        val accessToken = "test-token-123"
        val apiUsers = listOf(createSampleUserDto())
        val dbException = RuntimeException("Database save error")

        coEvery { userDao.getAllUsers() } returns emptyList()
        coEvery { storage.readSecureString(any()) } returns flowOf(accessToken)
        coEvery { userApi.getUsers(accessToken) } returns apiUsers
        coEvery { userDao.insertUsers(any()) } throws dbException

        // When
        val result = userRepository.getUsers()

        // Then
        assertTrue(result.isFailure)
        assertEquals(dbException, result.exceptionOrNull())
    }

    @Test
    fun `getUserById returns user when found in database`() = runTest {
        // Given
        val userId = 1
        val userEntity = createSampleUserEntity()
        val expectedUser = createSampleUserModel()

        coEvery { userDao.getUserById(userId) } returns userEntity

        // When
        val result = userRepository.getUserById(userId)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
        coVerify { userDao.getUserById(userId) }
    }

    @Test
    fun `getUserById returns null when user not found in database`() = runTest {
        // Given
        val userId = 999

        coEvery { userDao.getUserById(userId) } returns null

        // When
        val result = userRepository.getUserById(userId)

        // Then
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
        coVerify { userDao.getUserById(userId) }
    }

    @Test
    fun `getUserById handles database error gracefully`() = runTest {
        // Given
        val userId = 1
        val exception = RuntimeException("Database error")

        coEvery { userDao.getUserById(userId) } throws exception

        // When
        val result = userRepository.getUserById(userId)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { userDao.getUserById(userId) }
    }

}
