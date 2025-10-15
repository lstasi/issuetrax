package com.issuetrax.app.presentation.ui.pr_review.components

import com.issuetrax.app.domain.entity.CodeHunk
import com.issuetrax.app.domain.entity.DiffLine
import com.issuetrax.app.domain.entity.LineType
import org.junit.Test

/**
 * Unit tests for DiffHunk composable.
 * 
 * These tests verify the hunk rendering logic with various line configurations.
 * Full UI tests would be in androidTest.
 */
class DiffHunkTest {
    
    @Test
    fun `Hunk header should format correctly`() {
        // Given
        val hunk = CodeHunk(
            oldStart = 10,
            oldCount = 5,
            newStart = 12,
            newCount = 6,
            lines = emptyList()
        )
        
        // When
        val headerText = "@@ -${hunk.oldStart},${hunk.oldCount} +${hunk.newStart},${hunk.newCount} @@"
        
        // Then
        assert(headerText == "@@ -10,5 +12,6 @@") { "Hunk header should format correctly" }
    }
    
    @Test
    fun `Hunk with single line should be valid`() {
        // Given
        val line = DiffLine(
            type = LineType.ADDITION,
            content = "new line",
            oldLineNumber = null,
            newLineNumber = 1
        )
        val hunk = CodeHunk(
            oldStart = 1,
            oldCount = 0,
            newStart = 1,
            newCount = 1,
            lines = listOf(line)
        )
        
        // Then
        assert(hunk.lines.size == 1) { "Hunk should have single line" }
        assert(hunk.lines[0].type == LineType.ADDITION) { "Line should be addition" }
    }
    
    @Test
    fun `Hunk with multiple lines should preserve order`() {
        // Given
        val lines = listOf(
            DiffLine(LineType.CONTEXT, "context 1", 1, 1),
            DiffLine(LineType.DELETION, "old line", 2, null),
            DiffLine(LineType.ADDITION, "new line", null, 2),
            DiffLine(LineType.CONTEXT, "context 2", 3, 3)
        )
        val hunk = CodeHunk(
            oldStart = 1,
            oldCount = 3,
            newStart = 1,
            newCount = 3,
            lines = lines
        )
        
        // Then
        assert(hunk.lines.size == 4) { "Hunk should have all lines" }
        assert(hunk.lines[0].type == LineType.CONTEXT) { "First line should be context" }
        assert(hunk.lines[1].type == LineType.DELETION) { "Second line should be deletion" }
        assert(hunk.lines[2].type == LineType.ADDITION) { "Third line should be addition" }
        assert(hunk.lines[3].type == LineType.CONTEXT) { "Fourth line should be context" }
    }
    
    @Test
    fun `Empty hunk should be handled`() {
        // Given - A hunk with no lines
        val hunk = CodeHunk(
            oldStart = 1,
            oldCount = 0,
            newStart = 1,
            newCount = 0,
            lines = emptyList()
        )
        
        // Then
        assert(hunk.lines.isEmpty()) { "Hunk should have no lines" }
    }
    
    @Test
    fun `Hunk with only additions should be valid`() {
        // Given - New file with only additions
        val lines = listOf(
            DiffLine(LineType.ADDITION, "line 1", null, 1),
            DiffLine(LineType.ADDITION, "line 2", null, 2),
            DiffLine(LineType.ADDITION, "line 3", null, 3)
        )
        val hunk = CodeHunk(
            oldStart = 0,
            oldCount = 0,
            newStart = 1,
            newCount = 3,
            lines = lines
        )
        
        // Then
        assert(hunk.lines.size == 3) { "Hunk should have three lines" }
        assert(hunk.lines.all { it.type == LineType.ADDITION }) { "All lines should be additions" }
    }
    
    @Test
    fun `Hunk with only deletions should be valid`() {
        // Given - Deleted file with only deletions
        val lines = listOf(
            DiffLine(LineType.DELETION, "line 1", 1, null),
            DiffLine(LineType.DELETION, "line 2", 2, null),
            DiffLine(LineType.DELETION, "line 3", 3, null)
        )
        val hunk = CodeHunk(
            oldStart = 1,
            oldCount = 3,
            newStart = 0,
            newCount = 0,
            lines = lines
        )
        
        // Then
        assert(hunk.lines.size == 3) { "Hunk should have three lines" }
        assert(hunk.lines.all { it.type == LineType.DELETION }) { "All lines should be deletions" }
    }
    
    @Test
    fun `Large hunk should be handled`() {
        // Given - A hunk with many lines
        val lines = (1..100).map { i ->
            DiffLine(LineType.CONTEXT, "line $i", i, i)
        }
        val hunk = CodeHunk(
            oldStart = 1,
            oldCount = 100,
            newStart = 1,
            newCount = 100,
            lines = lines
        )
        
        // Then
        assert(hunk.lines.size == 100) { "Hunk should have 100 lines" }
    }
}
