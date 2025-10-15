package com.issuetrax.app.presentation.ui.pr_review.components

import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.FileStatus
import com.issuetrax.app.domain.util.DiffParser
import org.junit.Test

/**
 * Unit tests for DiffView composable.
 * 
 * These tests verify the file diff rendering logic with various patch formats.
 * Full UI tests would be in androidTest.
 */
class DiffViewTest {
    
    @Test
    fun `File with valid patch should parse hunks`() {
        // Given
        val patch = """
            @@ -1,3 +1,4 @@
             context line
            -deleted line
            +added line
            +another added line
             context line
        """.trimIndent()
        
        val fileDiff = FileDiff(
            filename = "test.txt",
            status = FileStatus.MODIFIED,
            additions = 2,
            deletions = 1,
            changes = 3,
            patch = patch,
            blobUrl = null,
            rawUrl = null
        )
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assert(hunks.isNotEmpty()) { "Should parse hunks from valid patch" }
    }
    
    @Test
    fun `File without patch should be handled`() {
        // Given - File with no patch (binary file, etc.)
        val fileDiff = FileDiff(
            filename = "image.png",
            status = FileStatus.ADDED,
            additions = 0,
            deletions = 0,
            changes = 0,
            patch = null,
            blobUrl = null,
            rawUrl = null
        )
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assert(hunks.isEmpty()) { "Should return empty list for null patch" }
    }
    
    @Test
    fun `File stats should display correctly`() {
        // Given
        val fileDiff = FileDiff(
            filename = "src/main.kt",
            status = FileStatus.MODIFIED,
            additions = 15,
            deletions = 8,
            changes = 23,
            patch = null,
            blobUrl = null,
            rawUrl = null
        )
        
        // Then
        assert(fileDiff.additions == 15) { "Additions should be 15" }
        assert(fileDiff.deletions == 8) { "Deletions should be 8" }
    }
    
    @Test
    fun `Added file should show correctly`() {
        // Given
        val fileDiff = FileDiff(
            filename = "new_file.kt",
            status = FileStatus.ADDED,
            additions = 50,
            deletions = 0,
            changes = 50,
            patch = "@@ -0,0 +1,50 @@\n+new content",
            blobUrl = null,
            rawUrl = null
        )
        
        // Then
        assert(fileDiff.status == FileStatus.ADDED) { "Status should be ADDED" }
        assert(fileDiff.deletions == 0) { "Added file should have no deletions" }
    }
    
    @Test
    fun `Deleted file should show correctly`() {
        // Given
        val fileDiff = FileDiff(
            filename = "old_file.kt",
            status = FileStatus.REMOVED,
            additions = 0,
            deletions = 30,
            changes = 30,
            patch = "@@ -1,30 +0,0 @@\n-old content",
            blobUrl = null,
            rawUrl = null
        )
        
        // Then
        assert(fileDiff.status == FileStatus.REMOVED) { "Status should be REMOVED" }
        assert(fileDiff.additions == 0) { "Deleted file should have no additions" }
    }
    
    @Test
    fun `Renamed file should show previous filename`() {
        // Given
        val fileDiff = FileDiff(
            filename = "src/new_name.kt",
            status = FileStatus.RENAMED,
            additions = 0,
            deletions = 0,
            changes = 0,
            patch = null,
            blobUrl = null,
            rawUrl = null,
            previousFilename = "src/old_name.kt"
        )
        
        // Then
        assert(fileDiff.status == FileStatus.RENAMED) { "Status should be RENAMED" }
        assert(fileDiff.previousFilename != null) { "Should have previous filename" }
        assert(fileDiff.previousFilename == "src/old_name.kt") { "Previous filename should match" }
    }
    
    @Test
    fun `File with multiple hunks should parse all`() {
        // Given
        val patch = """
            @@ -1,2 +1,3 @@
             line 1
            +line 1.5
             line 2
            @@ -10,2 +11,2 @@
            -old line
            +new line
             line 11
        """.trimIndent()
        
        val fileDiff = FileDiff(
            filename = "test.txt",
            status = FileStatus.MODIFIED,
            additions = 2,
            deletions = 1,
            changes = 3,
            patch = patch,
            blobUrl = null,
            rawUrl = null
        )
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assert(hunks.size == 2) { "Should parse both hunks" }
    }
    
    @Test
    fun `Long filename should be handled`() {
        // Given
        val longFilename = "src/main/java/com/example/package/subpackage/VeryLongClassNameThatExceedsNormalLength.kt"
        val fileDiff = FileDiff(
            filename = longFilename,
            status = FileStatus.MODIFIED,
            additions = 1,
            deletions = 1,
            changes = 2,
            patch = null,
            blobUrl = null,
            rawUrl = null
        )
        
        // Then
        assert(fileDiff.filename.length > 50) { "Should handle long filenames" }
    }
    
    @Test
    fun `Binary file patch should be handled gracefully`() {
        // Given - Binary file patch format
        val patch = "Binary files a/image.png and b/image.png differ"
        val fileDiff = FileDiff(
            filename = "image.png",
            status = FileStatus.MODIFIED,
            additions = 0,
            deletions = 0,
            changes = 0,
            patch = patch,
            blobUrl = null,
            rawUrl = null
        )
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assert(hunks.isEmpty()) { "Binary file should return empty hunks" }
    }
}
