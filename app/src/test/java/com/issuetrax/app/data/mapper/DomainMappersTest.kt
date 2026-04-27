package com.issuetrax.app.data.mapper

import com.issuetrax.app.data.api.model.RepositoryDto
import com.issuetrax.app.data.api.model.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDateTime

/**
 * Tests for DomainMappers extension functions, especially edge cases
 * in date parsing that could previously crash the app.
 */
class DomainMappersTest {

    private fun createTestUserDto() = UserDto(
        id = 1,
        login = "testuser",
        name = "Test User",
        email = "test@example.com",
        avatarUrl = "https://example.com/avatar.png",
        htmlUrl = "https://github.com/testuser",
        type = "User"
    )

    @Test
    fun `toDomain parses ISO date time with Z suffix`() {
        // Given - standard GitHub API format
        val repoDto = createTestRepoDto(
            createdAt = "2024-01-15T10:30:00Z",
            updatedAt = "2024-01-15T10:30:00Z"
        )

        // When
        val repo = repoDto.toDomain()

        // Then
        assertEquals(2024, repo.createdAt.year)
        assertEquals(1, repo.createdAt.monthValue)
        assertEquals(15, repo.createdAt.dayOfMonth)
    }

    @Test
    fun `toDomain parses ISO date time without Z suffix`() {
        // Given - alternative format
        val repoDto = createTestRepoDto(
            createdAt = "2024-01-15T10:30:00",
            updatedAt = "2024-01-15T10:30:00"
        )

        // When
        val repo = repoDto.toDomain()

        // Then
        assertEquals(2024, repo.createdAt.year)
    }

    @Test
    fun `toDomain handles malformed date gracefully without crashing`() {
        // Given - completely invalid date format that would previously crash
        val repoDto = createTestRepoDto(
            createdAt = "not-a-date",
            updatedAt = "also-invalid"
        )

        // When - this should NOT throw an exception
        val repo = repoDto.toDomain()

        // Then - should fall back to epoch start
        assertNotNull("Should return a non-null repository", repo)
        assertEquals(
            "Malformed date should fall back to epoch start",
            LocalDateTime.of(1970, 1, 1, 0, 0, 0),
            repo.createdAt
        )
    }

    @Test
    fun `toDomain handles empty date string gracefully without crashing`() {
        // Given
        val repoDto = createTestRepoDto(
            createdAt = "",
            updatedAt = ""
        )

        // When - should NOT throw
        val repo = repoDto.toDomain()

        // Then
        assertNotNull(repo)
        assertEquals(
            LocalDateTime.of(1970, 1, 1, 0, 0, 0),
            repo.createdAt
        )
    }

    private fun createTestRepoDto(
        createdAt: String = "2024-01-15T10:30:00Z",
        updatedAt: String = "2024-01-15T10:30:00Z"
    ) = RepositoryDto(
        id = 1,
        name = "test-repo",
        fullName = "testuser/test-repo",
        owner = createTestUserDto(),
        description = "A test repository",
        private = false,
        archived = false,
        htmlUrl = "https://github.com/testuser/test-repo",
        cloneUrl = "https://github.com/testuser/test-repo.git",
        sshUrl = "git@github.com:testuser/test-repo.git",
        defaultBranch = "main",
        language = "Kotlin",
        stargazersCount = 10,
        forksCount = 2,
        openIssuesCount = 5,
        createdAt = createdAt,
        updatedAt = updatedAt,
        pushedAt = null
    )
}
