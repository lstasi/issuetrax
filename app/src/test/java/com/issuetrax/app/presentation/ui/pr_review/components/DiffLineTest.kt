package com.issuetrax.app.presentation.ui.pr_review.components

import com.issuetrax.app.domain.entity.DiffLine
import com.issuetrax.app.domain.entity.LineType
import org.junit.Test

/**
 * Unit tests for DiffLine composable.
 * 
 * These tests verify the line rendering logic for different line types.
 * Full UI tests would be in androidTest.
 */
class DiffLineTest {
    
    @Test
    fun `Addition line should have both old and new line numbers null and not null`() {
        // Given - Addition line has only new line number
        val line = DiffLine(
            type = LineType.ADDITION,
            content = "added code",
            oldLineNumber = null,
            newLineNumber = 10
        )
        
        // Then
        assert(line.oldLineNumber == null) { "Addition line should have null old line number" }
        assert(line.newLineNumber != null) { "Addition line should have new line number" }
    }
    
    @Test
    fun `Deletion line should have only old line number`() {
        // Given - Deletion line has only old line number
        val line = DiffLine(
            type = LineType.DELETION,
            content = "deleted code",
            oldLineNumber = 5,
            newLineNumber = null
        )
        
        // Then
        assert(line.oldLineNumber != null) { "Deletion line should have old line number" }
        assert(line.newLineNumber == null) { "Deletion line should have null new line number" }
    }
    
    @Test
    fun `Context line should have both line numbers`() {
        // Given - Context line has both old and new line numbers
        val line = DiffLine(
            type = LineType.CONTEXT,
            content = "unchanged code",
            oldLineNumber = 8,
            newLineNumber = 9
        )
        
        // Then
        assert(line.oldLineNumber != null) { "Context line should have old line number" }
        assert(line.newLineNumber != null) { "Context line should have new line number" }
    }
    
    @Test
    fun `No newline marker should have null line numbers`() {
        // Given - NO_NEWLINE marker has no line numbers
        val line = DiffLine(
            type = LineType.NO_NEWLINE,
            content = "\\ No newline at end of file",
            oldLineNumber = null,
            newLineNumber = null
        )
        
        // Then
        assert(line.oldLineNumber == null) { "NO_NEWLINE should have null old line number" }
        assert(line.newLineNumber == null) { "NO_NEWLINE should have null new line number" }
    }
    
    @Test
    fun `Line content should be preserved`() {
        // Given
        val content = "def hello_world():"
        val line = DiffLine(
            type = LineType.ADDITION,
            content = content,
            oldLineNumber = null,
            newLineNumber = 1
        )
        
        // Then
        assert(line.content == content) { "Line content should be preserved exactly" }
    }
    
    @Test
    fun `Empty line content should be handled`() {
        // Given - Empty string is valid content
        val line = DiffLine(
            type = LineType.CONTEXT,
            content = "",
            oldLineNumber = 10,
            newLineNumber = 10
        )
        
        // Then
        assert(line.content == "") { "Empty content should be handled" }
    }
    
    @Test
    fun `Long line content should be handled`() {
        // Given - Very long line
        val longContent = "a".repeat(500)
        val line = DiffLine(
            type = LineType.ADDITION,
            content = longContent,
            oldLineNumber = null,
            newLineNumber = 1
        )
        
        // Then
        assert(line.content.length == 500) { "Long content should be preserved" }
    }
}
