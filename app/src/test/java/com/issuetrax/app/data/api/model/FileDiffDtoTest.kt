package com.issuetrax.app.data.api.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for FileDiffDto serialization/deserialization
 * 
 * Tests that the DTO correctly handles optional fields from GitHub API,
 * particularly the previous_filename field which is only present for renamed files.
 */
class FileDiffDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @Test
    fun `deserialize file with previous_filename should set previousFilename`() {
        // Given - JSON with previous_filename (renamed file)
        val jsonString = """
            {
                "filename": "config/SecurityConfig.kt",
                "status": "renamed",
                "additions": 5,
                "deletions": 2,
                "changes": 7,
                "patch": "@@ -1,5 +1,8 @@\n",
                "blob_url": "https://github.com/test/test/blob/abc123/config/SecurityConfig.kt",
                "raw_url": "https://github.com/test/test/raw/abc123/config/SecurityConfig.kt",
                "previous_filename": "config/Config.kt"
            }
        """.trimIndent()

        // When
        val fileDiff = json.decodeFromString<FileDiffDto>(jsonString)

        // Then
        assertEquals("config/SecurityConfig.kt", fileDiff.filename)
        assertEquals("renamed", fileDiff.status)
        assertEquals(5, fileDiff.additions)
        assertEquals(2, fileDiff.deletions)
        assertEquals(7, fileDiff.changes)
        assertEquals("@@ -1,5 +1,8 @@\n", fileDiff.patch)
        assertEquals("https://github.com/test/test/blob/abc123/config/SecurityConfig.kt", fileDiff.blobUrl)
        assertEquals("https://github.com/test/test/raw/abc123/config/SecurityConfig.kt", fileDiff.rawUrl)
        assertEquals("config/Config.kt", fileDiff.previousFilename)
    }

    @Test
    fun `deserialize file without previous_filename should default to null`() {
        // Given - JSON without previous_filename (modified file)
        val jsonString = """
            {
                "filename": "src/main/kotlin/AuthService.kt",
                "status": "modified",
                "additions": 45,
                "deletions": 12,
                "changes": 57,
                "patch": "@@ -1,10 +1,15 @@\n",
                "blob_url": "https://github.com/test/test/blob/abc123/src/main/kotlin/AuthService.kt",
                "raw_url": "https://github.com/test/test/raw/abc123/src/main/kotlin/AuthService.kt"
            }
        """.trimIndent()

        // When
        val fileDiff = json.decodeFromString<FileDiffDto>(jsonString)

        // Then
        assertEquals("src/main/kotlin/AuthService.kt", fileDiff.filename)
        assertEquals("modified", fileDiff.status)
        assertEquals(45, fileDiff.additions)
        assertEquals(12, fileDiff.deletions)
        assertEquals(57, fileDiff.changes)
        assertNull("previousFilename should be null when not present in JSON", fileDiff.previousFilename)
    }

    @Test
    fun `deserialize added file without previous_filename should default to null`() {
        // Given - JSON for added file (no previous_filename)
        val jsonString = """
            {
                "filename": "src/main/kotlin/OAuth2Config.kt",
                "status": "added",
                "additions": 78,
                "deletions": 0,
                "changes": 78,
                "patch": "@@ -0,0 +1,78 @@\n",
                "blob_url": "https://github.com/test/test/blob/abc123/src/main/kotlin/OAuth2Config.kt",
                "raw_url": "https://github.com/test/test/raw/abc123/src/main/kotlin/OAuth2Config.kt"
            }
        """.trimIndent()

        // When
        val fileDiff = json.decodeFromString<FileDiffDto>(jsonString)

        // Then
        assertEquals("src/main/kotlin/OAuth2Config.kt", fileDiff.filename)
        assertEquals("added", fileDiff.status)
        assertEquals(78, fileDiff.additions)
        assertEquals(0, fileDiff.deletions)
        assertEquals(78, fileDiff.changes)
        assertNull("previousFilename should be null for added files", fileDiff.previousFilename)
    }

    @Test
    fun `deserialize file with explicit null previous_filename should be null`() {
        // Given - JSON with explicit null value
        val jsonString = """
            {
                "filename": "README.md",
                "status": "modified",
                "additions": 15,
                "deletions": 3,
                "changes": 18,
                "patch": "@@ -10,7 +10,19 @@\n",
                "blob_url": "https://github.com/test/test/blob/abc123/README.md",
                "raw_url": "https://github.com/test/test/raw/abc123/README.md",
                "previous_filename": null
            }
        """.trimIndent()

        // When
        val fileDiff = json.decodeFromString<FileDiffDto>(jsonString)

        // Then
        assertEquals("README.md", fileDiff.filename)
        assertEquals("modified", fileDiff.status)
        assertNull("previousFilename should be null when explicitly set to null", fileDiff.previousFilename)
    }

    @Test
    fun `deserialize file array from GitHub API format`() {
        // Given - Array of files as returned by GitHub API
        val jsonString = """
            [
                {
                    "filename": "file1.kt",
                    "status": "modified",
                    "additions": 10,
                    "deletions": 5,
                    "changes": 15,
                    "patch": "@@ -1,3 +1,3 @@\n",
                    "blob_url": "https://github.com/test/test/blob/abc/file1.kt",
                    "raw_url": "https://github.com/test/test/raw/abc/file1.kt"
                },
                {
                    "filename": "file2.kt",
                    "status": "renamed",
                    "additions": 2,
                    "deletions": 1,
                    "changes": 3,
                    "patch": "@@ -1,2 +1,3 @@\n",
                    "blob_url": "https://github.com/test/test/blob/abc/file2.kt",
                    "raw_url": "https://github.com/test/test/raw/abc/file2.kt",
                    "previous_filename": "old_file2.kt"
                }
            ]
        """.trimIndent()

        // When
        val fileDiffs = json.decodeFromString<List<FileDiffDto>>(jsonString)

        // Then
        assertEquals(2, fileDiffs.size)
        
        // First file without previous_filename
        assertEquals("file1.kt", fileDiffs[0].filename)
        assertNull("First file should have null previousFilename", fileDiffs[0].previousFilename)
        
        // Second file with previous_filename
        assertEquals("file2.kt", fileDiffs[1].filename)
        assertEquals("old_file2.kt", fileDiffs[1].previousFilename)
    }
}
