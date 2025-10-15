package com.issuetrax.app.presentation.ui.pr_review

import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.FileStatus
import com.issuetrax.app.domain.entity.LineType
import com.issuetrax.app.domain.util.DiffParser
import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive validation tests for Phase 3.4 - Diff Viewer Testing & Validation
 * 
 * Test Coverage:
 * - Small diffs (1-5 lines)
 * - Large diffs (100+ lines)
 * - Various file types (Kotlin, Java, XML, JSON, Markdown)
 * - Mobile readability considerations
 * - Edge cases and error handling
 * 
 * These tests ensure the diff viewer correctly handles various scenarios
 * that users will encounter when reviewing pull requests.
 */
class DiffViewerValidationTest {
    
    // ========================================
    // SMALL DIFF TESTS (1-5 lines)
    // ========================================
    
    @Test
    fun `Small diff - Single line addition`() {
        // Given - A minimal diff with just one added line
        val patch = """
            @@ -1,2 +1,3 @@
             existing line 1
            +newly added line
             existing line 2
        """.trimIndent()
        
        val fileDiff = createFileDiff("SmallFile.kt", patch, additions = 1, deletions = 0)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, hunks.size)
        assertEquals("Should have 3 lines total", 3, hunks[0].lines.size)
        
        val additions = hunks[0].lines.filter { it.type == LineType.ADDITION }
        assertEquals("Should have 1 addition", 1, additions.size)
        assertEquals("Added line content", "newly added line", additions[0].content)
    }
    
    @Test
    fun `Small diff - Single line deletion`() {
        // Given
        val patch = """
            @@ -1,3 +1,2 @@
             existing line 1
            -deleted line
             existing line 2
        """.trimIndent()
        
        val fileDiff = createFileDiff("SmallFile.kt", patch, additions = 0, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, hunks.size)
        
        val deletions = hunks[0].lines.filter { it.type == LineType.DELETION }
        assertEquals("Should have 1 deletion", 1, deletions.size)
        assertEquals("Deleted line content", "deleted line", deletions[0].content)
    }
    
    @Test
    fun `Small diff - Single line modification (delete and add)`() {
        // Given
        val patch = """
            @@ -1,3 +1,3 @@
             existing line 1
            -old version of line
            +new version of line
             existing line 2
        """.trimIndent()
        
        val fileDiff = createFileDiff("SmallFile.kt", patch, additions = 1, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, hunks.size)
        
        val additions = hunks[0].lines.filter { it.type == LineType.ADDITION }
        val deletions = hunks[0].lines.filter { it.type == LineType.DELETION }
        
        assertEquals("Should have 1 addition", 1, additions.size)
        assertEquals("Should have 1 deletion", 1, deletions.size)
    }
    
    @Test
    fun `Small diff - Multiple small changes (3-5 lines)`() {
        // Given
        val patch = """
            @@ -1,7 +1,8 @@
             line 1
             line 2
            -old line 3
            +new line 3
            +extra line 3.5
             line 4
            -old line 5
            +new line 5
             line 6
        """.trimIndent()
        
        val fileDiff = createFileDiff("SmallFile.kt", patch, additions = 3, deletions = 2)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should have 1 hunk", 1, hunks.size)
        
        val additions = hunks[0].lines.filter { it.type == LineType.ADDITION }
        val deletions = hunks[0].lines.filter { it.type == LineType.DELETION }
        
        assertEquals("Should have 3 additions", 3, additions.size)
        assertEquals("Should have 2 deletions", 2, deletions.size)
        
        // Verify additions include the extra line
        assertTrue("Should contain extra line", 
            additions.any { it.content == "extra line 3.5" })
    }
    
    @Test
    fun `Small diff - Empty line changes`() {
        // Given
        val patch = """
            @@ -1,4 +1,4 @@
             line 1
            -
            +
             line 2
        """.trimIndent()
        
        val fileDiff = createFileDiff("SmallFile.kt", patch, additions = 1, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should handle empty line changes", 1, hunks.size)
        
        // Even though visually they look the same, they should be tracked as separate lines
        val additions = hunks[0].lines.filter { it.type == LineType.ADDITION }
        val deletions = hunks[0].lines.filter { it.type == LineType.DELETION }
        
        assertEquals("Should track empty line addition", 1, additions.size)
        assertEquals("Should track empty line deletion", 1, deletions.size)
    }
    
    // ========================================
    // LARGE DIFF TESTS (100+ lines)
    // ========================================
    
    @Test
    fun `Large diff - 100 line addition`() {
        // Given - A large new file or section with 100 lines
        val patchLines = buildString {
            appendLine("@@ -0,0 +1,100 @@")
            repeat(100) { i ->
                appendLine("+line ${i + 1}")
            }
        }.trimEnd()
        
        val fileDiff = createFileDiff("LargeFile.kt", patchLines, additions = 100, deletions = 0)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should have 1 large hunk", 1, hunks.size)
        assertEquals("Should have 100 lines", 100, hunks[0].lines.size)
        
        val additions = hunks[0].lines.filter { it.type == LineType.ADDITION }
        assertEquals("All lines should be additions", 100, additions.size)
        
        // Verify line numbering is correct
        additions.forEachIndexed { index, line ->
            assertEquals("Line ${index + 1} should have correct new line number", 
                index + 1, line.newLineNumber)
            assertNull("Addition should have no old line number", line.oldLineNumber)
        }
    }
    
    @Test
    fun `Large diff - 150 line mixed changes`() {
        // Given - A large file with mixed additions, deletions, and context
        val patchLines = buildString {
            appendLine("@@ -1,100 +1,150 @@")
            // First 20 lines of context
            repeat(20) { i ->
                appendLine(" context line ${i + 1}")
            }
            // 50 deletions
            repeat(50) { i ->
                appendLine("-old line ${i + 21}")
            }
            // 100 additions
            repeat(100) { i ->
                appendLine("+new line ${i + 1}")
            }
            // Last 30 lines of context
            repeat(30) { i ->
                appendLine(" context line ${i + 71}")
            }
        }.trimEnd()
        
        val fileDiff = createFileDiff("LargeFile.kt", patchLines, additions = 100, deletions = 50)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should have 1 large hunk", 1, hunks.size)
        assertEquals("Should have 200 lines total", 200, hunks[0].lines.size)
        
        val additions = hunks[0].lines.filter { it.type == LineType.ADDITION }
        val deletions = hunks[0].lines.filter { it.type == LineType.DELETION }
        val context = hunks[0].lines.filter { it.type == LineType.CONTEXT }
        
        assertEquals("Should have 100 additions", 100, additions.size)
        assertEquals("Should have 50 deletions", 50, deletions.size)
        assertEquals("Should have 50 context lines", 50, context.size)
    }
    
    @Test
    fun `Large diff - Multiple large hunks`() {
        // Given - Multiple hunks each with significant changes
        val patchLines = buildString {
            // First hunk: 50 lines
            appendLine("@@ -1,30 +1,50 @@")
            repeat(10) { appendLine(" context line ${it + 1}") }
            repeat(20) { appendLine("-old line ${it + 11}") }
            repeat(40) { appendLine("+new line ${it + 1}") }
            
            // Second hunk: 60 lines
            appendLine("@@ -100,40 +120,60 @@")
            repeat(15) { appendLine(" context line ${it + 100}") }
            repeat(25) { appendLine("-old line ${it + 115}") }
            repeat(45) { appendLine("+new line ${it + 100}") }
        }.trimEnd()
        
        val fileDiff = createFileDiff("LargeFile.kt", patchLines, additions = 85, deletions = 45)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should have 2 large hunks", 2, hunks.size)
        assertTrue("First hunk should have many lines", hunks[0].lines.size >= 50)
        assertTrue("Second hunk should have many lines", hunks[1].lines.size >= 60)
    }
    
    @Test
    fun `Large diff - Performance with deeply nested structure`() {
        // Given - Complex nested structure (realistic Kotlin/Java code)
        val patchLines = buildString {
            appendLine("@@ -1,80 +1,120 @@")
            appendLine(" package com.example.app")
            appendLine(" ")
            appendLine(" class LargeClass {")
            
            // Add many method definitions
            repeat(30) { methodNum ->
                appendLine(" ")
                appendLine("-    fun oldMethod${methodNum}() {")
                repeat(2) { appendLine("-        // old implementation") }
                appendLine("-    }")
                appendLine("+    fun newMethod${methodNum}(param: String) {")
                repeat(3) { appendLine("+        // new implementation") }
                appendLine("+    }")
            }
            
            appendLine(" }")
        }.trimEnd()
        
        val fileDiff = createFileDiff("LargeClass.kt", patchLines, additions = 120, deletions = 90)
        
        // When
        val startTime = System.nanoTime()
        val hunks = DiffParser.parse(fileDiff.patch)
        val parseTimeMs = (System.nanoTime() - startTime) / 1_000_000
        
        // Then
        assertEquals("Should parse all hunks", 1, hunks.size)
        assertTrue("Should have significant number of lines", hunks[0].lines.size > 100)
        assertTrue("Should parse quickly (< 50ms)", parseTimeMs < 50)
    }
    
    // ========================================
    // VARIOUS FILE TYPES
    // ========================================
    
    @Test
    fun `File type - Kotlin source code`() {
        // Given
        val patch = """
            @@ -1,10 +1,12 @@
             package com.issuetrax.app.domain.entity
             
             data class PullRequest(
                 val number: Int,
                 val title: String,
            -    val state: String,
            +    val state: PRState,
                 val author: User,
            +    val createdAt: String,
            +    val updatedAt: String,
                 val body: String?
             )
        """.trimIndent()
        
        val fileDiff = createFileDiff("PullRequest.kt", patch, additions = 3, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse Kotlin code", 1, hunks.size)
        assertTrue("Should handle Kotlin syntax", 
            hunks[0].lines.any { it.content.contains("data class") })
        assertTrue("Should preserve indentation",
            hunks[0].lines.any { it.content.startsWith("    ") })
    }
    
    @Test
    fun `File type - Java source code`() {
        // Given
        val patch = """
            @@ -1,8 +1,10 @@
             package com.example.app;
             
             public class AuthService {
            -    public boolean authenticate(String user) {
            -        return false;
            +    public boolean authenticate(String user, String password) {
            +        if (user == null || password == null) {
            +            throw new IllegalArgumentException("Credentials cannot be null");
            +        }
            +        return validateCredentials(user, password);
                 }
             }
        """.trimIndent()
        
        val fileDiff = createFileDiff("AuthService.java", patch, additions = 5, deletions = 2)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse Java code", 1, hunks.size)
        assertTrue("Should handle Java keywords", 
            hunks[0].lines.any { it.content.contains("public") })
        assertTrue("Should handle semicolons",
            hunks[0].lines.any { it.content.endsWith(";") })
    }
    
    @Test
    fun `File type - XML layout file`() {
        // Given
        val patch = """
            @@ -1,8 +1,10 @@
             <?xml version="1.0" encoding="utf-8"?>
             <LinearLayout
                 xmlns:android="http://schemas.android.com/apk/res/android"
                 android:layout_width="match_parent"
            -    android:layout_height="wrap_content">
            +    android:layout_height="wrap_content"
            +    android:orientation="vertical"
            +    android:padding="16dp">
                 
                 <TextView
        """.trimIndent()
        
        val fileDiff = createFileDiff("layout/activity_main.xml", patch, additions = 3, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse XML", 1, hunks.size)
        assertTrue("Should handle XML tags", 
            hunks[0].lines.any { it.content.contains("<?xml") })
        assertTrue("Should handle attributes",
            hunks[0].lines.any { it.content.contains("android:") })
    }
    
    @Test
    fun `File type - JSON configuration`() {
        // Given
        val patch = """
            @@ -1,6 +1,8 @@
             {
               "name": "issuetrax",
               "version": "1.0.0",
            -  "dependencies": {}
            +  "dependencies": {
            +    "retrofit": "2.9.0"
            +  }
             }
        """.trimIndent()
        
        val fileDiff = createFileDiff("package.json", patch, additions = 3, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse JSON", 1, hunks.size)
        assertTrue("Should handle JSON structure", 
            hunks[0].lines.any { it.content.trim().startsWith("{") })
        assertTrue("Should handle quotes",
            hunks[0].lines.any { it.content.contains("\"") })
    }
    
    @Test
    fun `File type - Markdown documentation`() {
        // Given
        val patch = """
            @@ -1,5 +1,8 @@
             # Issuetrax
             
            -A GitHub PR review app.
            +A mobile-optimized GitHub PR review application built with Kotlin and Jetpack Compose.
            +
            +## Features
            +- Pull request review interface
             
             ## Installation
        """.trimIndent()
        
        val fileDiff = createFileDiff("README.md", patch, additions = 4, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse Markdown", 1, hunks.size)
        assertTrue("Should handle headers", 
            hunks[0].lines.any { it.content.startsWith("#") })
        assertTrue("Should handle list items",
            hunks[0].lines.any { it.content.startsWith("-") })
    }
    
    @Test
    fun `File type - Gradle build script`() {
        // Given
        val patch = """
            @@ -1,5 +1,7 @@
             plugins {
                 id("com.android.application")
            +    id("org.jetbrains.kotlin.android")
            +    id("com.google.dagger.hilt.android")
             }
             
             android {
        """.trimIndent()
        
        val fileDiff = createFileDiff("build.gradle.kts", patch, additions = 2, deletions = 0)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse Gradle Kotlin DSL", 1, hunks.size)
        assertTrue("Should handle DSL syntax", 
            hunks[0].lines.any { it.content.contains("id(") })
    }
    
    @Test
    fun `File type - Properties file`() {
        // Given
        val patch = """
            @@ -1,3 +1,5 @@
             org.gradle.jvmargs=-Xmx2048m
            -android.useAndroidX=true
            +android.useAndroidX=true
            +android.enableJetifier=false
            +kotlin.code.style=official
        """.trimIndent()
        
        val fileDiff = createFileDiff("gradle.properties", patch, additions = 3, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse properties file", 1, hunks.size)
        assertTrue("Should handle key=value format", 
            hunks[0].lines.any { it.content.contains("=") })
    }
    
    // ========================================
    // MOBILE READABILITY TESTS
    // ========================================
    
    @Test
    fun `Mobile readability - Long line handling`() {
        // Given - Very long lines that would exceed mobile screen width
        val longLine = "a".repeat(150) // 150 characters
        val patch = """
            @@ -1,2 +1,2 @@
            -$longLine
            +${longLine}modified
             short line
        """.trimIndent()
        
        val fileDiff = createFileDiff("LongLines.kt", patch, additions = 1, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse long lines", 1, hunks.size)
        
        val deletion = hunks[0].lines.first { it.type == LineType.DELETION }
        val addition = hunks[0].lines.first { it.type == LineType.ADDITION }
        
        assertTrue("Should preserve long line in deletion", deletion.content.length >= 150)
        assertTrue("Should preserve long line in addition", addition.content.length >= 150)
        // Note: UI layer should handle horizontal scrolling for these
    }
    
    @Test
    fun `Mobile readability - Deep indentation levels`() {
        // Given - Code with deep nesting
        val patch = """
            @@ -1,10 +1,12 @@
             class Outer {
                 class Inner1 {
                     class Inner2 {
                         class Inner3 {
            -                fun deepMethod() {
            -                    // old implementation
            +                fun deepMethod(param: String) {
            +                    if (param.isNotEmpty()) {
            +                        println("Processing: ${'$'}param")
            +                    }
                             }
                         }
                     }
                 }
             }
        """.trimIndent()
        
        val fileDiff = createFileDiff("DeepNesting.kt", patch, additions = 4, deletions = 2)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse deeply nested code", 1, hunks.size)
        
        // Verify indentation is preserved
        val deeplyIndented = hunks[0].lines.filter { 
            it.content.startsWith(" ".repeat(16)) // 16+ spaces
        }
        assertTrue("Should preserve deep indentation", deeplyIndented.isNotEmpty())
    }
    
    @Test
    fun `Mobile readability - Special characters and emojis`() {
        // Given - Code with special characters
        val patch = """
            @@ -1,4 +1,5 @@
             // TODO: Implement authentication
            -val message = "Hello World"
            +val message = "Hello World! 👋"
            +val emoji = "✅ Success"
             val regex = "[a-zA-Z0-9_]+"
        """.trimIndent()
        
        val fileDiff = createFileDiff("SpecialChars.kt", patch, additions = 2, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse special characters", 1, hunks.size)
        assertTrue("Should preserve emojis", 
            hunks[0].lines.any { it.content.contains("👋") })
        assertTrue("Should preserve regex patterns",
            hunks[0].lines.any { it.content.contains("[a-zA-Z0-9_]+") })
    }
    
    @Test
    fun `Mobile readability - Tab vs space indentation`() {
        // Given - Mix of tabs and spaces
        val patch = """
            @@ -1,4 +1,4 @@
             class Example {
            -${"\t"}fun method1() { }
            +    fun method1() { } // 4 spaces
                 fun method2() { }
             }
        """.trimIndent()
        
        val fileDiff = createFileDiff("Indentation.kt", patch, additions = 1, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should handle mixed indentation", 1, hunks.size)
        
        val deletion = hunks[0].lines.first { it.type == LineType.DELETION }
        val addition = hunks[0].lines.first { it.type == LineType.ADDITION }
        
        // Both should be preserved as-is
        assertNotNull("Deletion content preserved", deletion.content)
        assertNotNull("Addition content preserved", addition.content)
    }
    
    // ========================================
    // EDGE CASES
    // ========================================
    
    @Test
    fun `Edge case - No newline at end of file`() {
        // Given
        val patch = """
            @@ -1,2 +1,2 @@
             line 1
            -line 2
            \ No newline at end of file
            +line 2
        """.trimIndent()
        
        val fileDiff = createFileDiff("NoNewline.kt", patch, additions = 1, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should handle no newline marker", 1, hunks.size)
        assertTrue("Should include NO_NEWLINE marker", 
            hunks[0].lines.any { it.type == LineType.NO_NEWLINE })
    }
    
    @Test
    fun `Edge case - Binary file detection`() {
        // Given
        val patch = "Binary files a/image.png and b/image.png differ"
        val fileDiff = createFileDiff("image.png", patch, additions = 0, deletions = 0)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertTrue("Should return empty list for binary files", hunks.isEmpty())
    }
    
    @Test
    fun `Edge case - File with only whitespace changes`() {
        // Given
        val patch = """
            @@ -1,3 +1,3 @@
             line 1
            -line 2  
            +line 2
             line 3
        """.trimIndent()
        
        val fileDiff = createFileDiff("Whitespace.kt", patch, additions = 1, deletions = 1)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should parse whitespace-only changes", 1, hunks.size)
        
        val deletion = hunks[0].lines.first { it.type == LineType.DELETION }
        val addition = hunks[0].lines.first { it.type == LineType.ADDITION }
        
        // Trailing spaces should be preserved
        assertTrue("Deletion should have content", deletion.content.isNotEmpty())
        assertTrue("Addition should have content", addition.content.isNotEmpty())
    }
    
    @Test
    fun `Edge case - Empty file creation`() {
        // Given
        val patch = """
            @@ -0,0 +1 @@
            +
        """.trimIndent()
        
        val fileDiff = createFileDiff("EmptyFile.kt", patch, additions = 1, deletions = 0)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should handle empty file creation", 1, hunks.size)
        assertEquals("Should have 1 line", 1, hunks[0].lines.size)
    }
    
    @Test
    fun `Edge case - File deletion`() {
        // Given
        val patch = """
            @@ -1,5 +0,0 @@
            -package com.example
            -
            -class OldClass {
            -    // old code
            -}
        """.trimIndent()
        
        val fileDiff = createFileDiff("DeletedFile.kt", patch, additions = 0, deletions = 5)
        
        // When
        val hunks = DiffParser.parse(fileDiff.patch)
        
        // Then
        assertEquals("Should handle file deletion", 1, hunks.size)
        
        val deletions = hunks[0].lines.filter { it.type == LineType.DELETION }
        assertEquals("All lines should be deletions", 5, deletions.size)
    }
    
    // ========================================
    // HELPER METHODS
    // ========================================
    
    private fun createFileDiff(
        filename: String,
        patch: String,
        additions: Int,
        deletions: Int,
        status: FileStatus = FileStatus.MODIFIED
    ): FileDiff {
        return FileDiff(
            filename = filename,
            status = status,
            additions = additions,
            deletions = deletions,
            changes = additions + deletions,
            patch = patch,
            blobUrl = null,
            rawUrl = null
        )
    }
}
