package com.issuetrax.app.domain.util

import com.issuetrax.app.domain.entity.LineType
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for DiffParser
 * 
 * Tests cover:
 * - Basic diff parsing (additions, deletions, context)
 * - Hunk header parsing with various formats
 * - Multiple hunks in single patch
 * - Edge cases (empty patch, null patch, binary files)
 * - Special markers (no newline at EOF)
 * - Real-world examples from GitHub API
 */
class DiffParserTest {
    
    @Test
    fun `parse returns empty list for null patch`() {
        // Given
        val patch: String? = null
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertTrue("Should return empty list for null patch", result.isEmpty())
    }
    
    @Test
    fun `parse returns empty list for empty patch`() {
        // Given
        val patch = ""
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertTrue("Should return empty list for empty patch", result.isEmpty())
    }
    
    @Test
    fun `parse returns empty list for blank patch`() {
        // Given
        val patch = "   \n  \n  "
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertTrue("Should return empty list for blank patch", result.isEmpty())
    }
    
    @Test
    fun `parse returns empty list for binary file`() {
        // Given
        val patch = "Binary files a/image.png and b/image.png differ"
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertTrue("Should return empty list for binary files", result.isEmpty())
    }
    
    @Test
    fun `parse returns empty list for GIT binary patch`() {
        // Given
        val patch = "GIT binary patch\nliteral 1234\nabcdef..."
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertTrue("Should return empty list for GIT binary patch", result.isEmpty())
    }
    
    @Test
    fun `parse single hunk with additions only`() {
        // Given
        val patch = """
            @@ -0,0 +1,3 @@
            +package com.example
            +
            +class NewClass {}
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, result.size)
        
        val hunk = result[0]
        assertEquals("Old start should be 0", 0, hunk.oldStart)
        assertEquals("Old count should be 0", 0, hunk.oldCount)
        assertEquals("New start should be 1", 1, hunk.newStart)
        assertEquals("New count should be 3", 3, hunk.newCount)
        assertEquals("Should have 3 lines", 3, hunk.lines.size)
        
        // Verify all lines are additions
        hunk.lines.forEach { line ->
            assertEquals("Line should be addition", LineType.ADDITION, line.type)
            assertNull("Addition should have no old line number", line.oldLineNumber)
            assertNotNull("Addition should have new line number", line.newLineNumber)
        }
        
        // Verify content
        assertEquals("package com.example", hunk.lines[0].content)
        assertEquals("", hunk.lines[1].content)
        assertEquals("class NewClass {}", hunk.lines[2].content)
    }
    
    @Test
    fun `parse single hunk with deletions only`() {
        // Given
        val patch = """
            @@ -1,2 +0,0 @@
            -old line 1
            -old line 2
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, result.size)
        
        val hunk = result[0]
        assertEquals("Old start should be 1", 1, hunk.oldStart)
        assertEquals("Old count should be 2", 2, hunk.oldCount)
        assertEquals("New start should be 0", 0, hunk.newStart)
        assertEquals("New count should be 0", 0, hunk.newCount)
        assertEquals("Should have 2 lines", 2, hunk.lines.size)
        
        // Verify all lines are deletions
        hunk.lines.forEach { line ->
            assertEquals("Line should be deletion", LineType.DELETION, line.type)
            assertNotNull("Deletion should have old line number", line.oldLineNumber)
            assertNull("Deletion should have no new line number", line.newLineNumber)
        }
    }
    
    @Test
    fun `parse single hunk with mixed changes`() {
        // Given
        val patch = """
            @@ -1,5 +1,6 @@
             package com.example
             
             class AuthService {
            -    fun authenticate(user: String): Boolean {
            -        return false
            +    fun authenticate(user: String, password: String): Boolean {
            +        return validateCredentials(user, password)
             }
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, result.size)
        
        val hunk = result[0]
        assertEquals("Old start should be 1", 1, hunk.oldStart)
        assertEquals("Old count should be 5", 5, hunk.oldCount)
        assertEquals("New start should be 1", 1, hunk.newStart)
        assertEquals("New count should be 6", 6, hunk.newCount)
        
        // Verify line types
        assertEquals("First line should be context", LineType.CONTEXT, hunk.lines[0].type)
        assertEquals("Second line should be context", LineType.CONTEXT, hunk.lines[1].type)
        assertEquals("Third line should be context", LineType.CONTEXT, hunk.lines[2].type)
        assertEquals("Fourth line should be deletion", LineType.DELETION, hunk.lines[3].type)
        assertEquals("Fifth line should be deletion", LineType.DELETION, hunk.lines[4].type)
        assertEquals("Sixth line should be addition", LineType.ADDITION, hunk.lines[5].type)
        assertEquals("Seventh line should be addition", LineType.ADDITION, hunk.lines[6].type)
        assertEquals("Eighth line should be context", LineType.CONTEXT, hunk.lines[7].type)
    }
    
    @Test
    fun `parse multiple hunks in single patch`() {
        // Given
        val patch = """
            @@ -1,3 +1,3 @@
             line 1
            -old line 2
            +new line 2
             line 3
            @@ -10,2 +10,3 @@
             line 10
            +added line 11
             line 12
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 2 hunks", 2, result.size)
        
        // First hunk
        val hunk1 = result[0]
        assertEquals("Hunk 1: old start", 1, hunk1.oldStart)
        assertEquals("Hunk 1: old count", 3, hunk1.oldCount)
        assertEquals("Hunk 1: new start", 1, hunk1.newStart)
        assertEquals("Hunk 1: new count", 3, hunk1.newCount)
        assertEquals("Hunk 1: should have 4 lines", 4, hunk1.lines.size)
        
        // Second hunk
        val hunk2 = result[1]
        assertEquals("Hunk 2: old start", 10, hunk2.oldStart)
        assertEquals("Hunk 2: old count", 2, hunk2.oldCount)
        assertEquals("Hunk 2: new start", 10, hunk2.newStart)
        assertEquals("Hunk 2: new count", 3, hunk2.newCount)
        assertEquals("Hunk 2: should have 3 lines", 3, hunk2.lines.size)
    }
    
    @Test
    fun `parse hunk with no newline at end of file marker`() {
        // Given
        val patch = """
            @@ -1,2 +1,2 @@
             line 1
            -old line 2
            \ No newline at end of file
            +new line 2
            \ No newline at end of file
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, result.size)
        
        val hunk = result[0]
        assertEquals("Should have 5 lines", 5, hunk.lines.size)
        
        // Verify line types
        assertEquals("First line: context", LineType.CONTEXT, hunk.lines[0].type)
        assertEquals("Second line: deletion", LineType.DELETION, hunk.lines[1].type)
        assertEquals("Third line: no newline marker", LineType.NO_NEWLINE, hunk.lines[2].type)
        assertEquals("Fourth line: addition", LineType.ADDITION, hunk.lines[3].type)
        assertEquals("Fifth line: no newline marker", LineType.NO_NEWLINE, hunk.lines[4].type)
        
        // Verify no newline markers have no line numbers
        assertNull("No newline marker: no old line number", hunk.lines[2].oldLineNumber)
        assertNull("No newline marker: no new line number", hunk.lines[2].newLineNumber)
    }
    
    @Test
    fun `parse hunk header with single line change (no count)`() {
        // Given - when count is 1, it may be omitted in the header
        val patch = """
            @@ -5 +5 @@
            -old line
            +new line
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, result.size)
        
        val hunk = result[0]
        assertEquals("Old start should be 5", 5, hunk.oldStart)
        assertEquals("Old count should default to 1", 1, hunk.oldCount)
        assertEquals("New start should be 5", 5, hunk.newStart)
        assertEquals("New count should default to 1", 1, hunk.newCount)
    }
    
    @Test
    fun `parse hunk header with section name`() {
        // Given - GitHub includes function/section names in headers
        val patch = """
            @@ -15,7 +15,8 @@ class AuthService {
             context line
            +added line
             context line
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, result.size)
        assertEquals("Old start should be 15", 15, result[0].oldStart)
        assertEquals("New count should be 8", 8, result[0].newCount)
    }
    
    @Test
    fun `parse tracks line numbers correctly for context lines`() {
        // Given
        val patch = """
            @@ -10,4 +10,4 @@
             context line 10
             context line 11
            -old line 12
            +new line 12
             context line 13
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        val hunk = result[0]
        
        // Context line 10
        assertEquals("Line 0: old number", 10, hunk.lines[0].oldLineNumber)
        assertEquals("Line 0: new number", 10, hunk.lines[0].newLineNumber)
        
        // Context line 11
        assertEquals("Line 1: old number", 11, hunk.lines[1].oldLineNumber)
        assertEquals("Line 1: new number", 11, hunk.lines[1].newLineNumber)
        
        // Deletion at old line 12
        assertEquals("Line 2: old number", 12, hunk.lines[2].oldLineNumber)
        assertNull("Line 2: no new number", hunk.lines[2].newLineNumber)
        
        // Addition at new line 12
        assertNull("Line 3: no old number", hunk.lines[3].oldLineNumber)
        assertEquals("Line 3: new number", 12, hunk.lines[3].newLineNumber)
        
        // Context line 13 (old) / 13 (new)
        assertEquals("Line 4: old number", 13, hunk.lines[4].oldLineNumber)
        assertEquals("Line 4: new number", 13, hunk.lines[4].newLineNumber)
    }
    
    @Test
    fun `parse real-world GitHub diff from test resources`() {
        // Given - This is the actual format from GitHub API
        val patch = """@@ -1,10 +1,15 @@
 package com.example
 
 class AuthService {
-    fun authenticate(user: String): Boolean {
-        return false
+    fun authenticate(user: String, password: String): Boolean {
+        if (user.isEmpty() || password.isEmpty()) {
+            return false
+        }
+        return validateCredentials(user, password)
     }
+    
+    private fun validateCredentials(user: String, password: String): Boolean {
+        // Implementation
+        return true
+    }
 }"""
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, result.size)
        
        val hunk = result[0]
        assertEquals("Old start should be 1", 1, hunk.oldStart)
        assertEquals("Old count should be 10", 10, hunk.oldCount)
        assertEquals("New start should be 1", 1, hunk.newStart)
        assertEquals("New count should be 15", 15, hunk.newCount)
        
        // Should have 3 context + 2 deletions + 8 additions + 2 context = 15 lines
        assertTrue("Should have at least 13 lines", hunk.lines.size >= 13)
        
        // Verify first line is context
        assertEquals("First line should be context", LineType.CONTEXT, hunk.lines[0].type)
        assertEquals("First line content", "package com.example", hunk.lines[0].content)
        
        // Find deletions and additions
        val deletions = hunk.lines.filter { it.type == LineType.DELETION }
        val additions = hunk.lines.filter { it.type == LineType.ADDITION }
        
        assertEquals("Should have 2 deletions", 2, deletions.size)
        assertTrue("Should have multiple additions", additions.size >= 5)
    }
    
    @Test
    fun `parse handles empty lines in diff correctly`() {
        // Given
        val patch = """
            @@ -1,4 +1,5 @@
             line 1
             
            +added line
             
             line 4
        """.trimIndent()
        
        // When
        val result = DiffParser.parse(patch)
        
        // Then
        val hunk = result[0]
        assertEquals("Should have 5 lines", 5, hunk.lines.size)
        
        // Empty context lines should be preserved
        assertEquals("Second line should be context", LineType.CONTEXT, hunk.lines[1].type)
        assertEquals("Second line should be empty", "", hunk.lines[1].content)
    }
}
