package com.issuetrax.app.domain.util

import com.issuetrax.app.domain.entity.CodeHunk
import com.issuetrax.app.domain.entity.DiffLine
import com.issuetrax.app.domain.entity.LineType

/**
 * Utility class for parsing unified diff format patches from GitHub PR files.
 * 
 * Handles:
 * - Parsing unified diff format from FileDiff.patch
 * - Extracting code hunks with line numbers
 * - Identifying additions (+), deletions (-), and context lines
 * - Special cases like binary files and "no newline at EOF"
 * 
 * Unified diff format structure:
 * ```
 * @@ -oldStart,oldCount +newStart,newCount @@ optional section header
 * context line
 * -deleted line
 * +added line
 * context line
 * \ No newline at end of file
 * ```
 */
object DiffParser {
    
    /**
     * Regex pattern for parsing hunk headers.
     * Format: @@ -oldStart[,oldCount] +newStart[,newCount] @@ [section header]
     * Capture groups:
     * - Group 1: oldStart (line number where changes start in old file)
     * - Group 2: oldCount (number of lines in old file, optional, defaults to 1)
     * - Group 3: newStart (line number where changes start in new file)
     * - Group 4: newCount (number of lines in new file, optional, defaults to 1)
     */
    private val HUNK_HEADER_REGEX = Regex("""^@@\s+-(\d+)(?:,(\d+))?\s+\+(\d+)(?:,(\d+))?\s+@@.*$""")
    private const val NO_NEWLINE_MARKER = "\\ No newline at end of file"
    
    /**
     * Parse a unified diff patch into a list of code hunks.
     * 
     * @param patch The unified diff patch string from GitHub API
     * @return List of CodeHunk objects, or empty list if patch is null/empty or binary
     */
    fun parse(patch: String?): List<CodeHunk> {
        if (patch.isNullOrBlank()) {
            return emptyList()
        }
        
        // Check for binary file indicator
        if (patch.contains("Binary files") || patch.startsWith("GIT binary patch")) {
            return emptyList()
        }
        
        val hunks = mutableListOf<CodeHunk>()
        val lines = patch.lines()
        
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            
            // Look for hunk header
            val match = HUNK_HEADER_REGEX.matchEntire(line)
            if (match != null) {
                val hunkHeader = parseHunkHeader(match)
                val hunkLines = mutableListOf<DiffLine>()
                
                var oldLineNum = hunkHeader.oldStart
                var newLineNum = hunkHeader.newStart
                
                // Parse lines in this hunk
                i++
                while (i < lines.size) {
                    val hunkLine = lines[i]
                    
                    // Check if we've reached the next hunk header
                    if (hunkLine.startsWith("@@")) {
                        break
                    }
                    
                    when {
                        hunkLine.startsWith("+") -> {
                            // Addition
                            hunkLines.add(
                                DiffLine(
                                    type = LineType.ADDITION,
                                    content = hunkLine.substring(1),
                                    oldLineNumber = null,
                                    newLineNumber = newLineNum
                                )
                            )
                            newLineNum++
                        }
                        hunkLine.startsWith("-") -> {
                            // Deletion
                            hunkLines.add(
                                DiffLine(
                                    type = LineType.DELETION,
                                    content = hunkLine.substring(1),
                                    oldLineNumber = oldLineNum,
                                    newLineNumber = null
                                )
                            )
                            oldLineNum++
                        }
                        hunkLine.startsWith("\\") -> {
                            // Special marker like "\ No newline at end of file"
                            hunkLines.add(
                                DiffLine(
                                    type = LineType.NO_NEWLINE,
                                    content = hunkLine,
                                    oldLineNumber = null,
                                    newLineNumber = null
                                )
                            )
                        }
                        else -> {
                            // Context line (or empty line, which is also context)
                            // Context lines start with space or are empty
                            val content = if (hunkLine.startsWith(" ")) {
                                hunkLine.substring(1)
                            } else {
                                hunkLine
                            }
                            hunkLines.add(
                                DiffLine(
                                    type = LineType.CONTEXT,
                                    content = content,
                                    oldLineNumber = oldLineNum,
                                    newLineNumber = newLineNum
                                )
                            )
                            oldLineNum++
                            newLineNum++
                        }
                    }
                    
                    i++
                }
                
                hunks.add(
                    CodeHunk(
                        oldStart = hunkHeader.oldStart,
                        oldCount = hunkHeader.oldCount,
                        newStart = hunkHeader.newStart,
                        newCount = hunkHeader.newCount,
                        lines = hunkLines
                    )
                )
            } else {
                i++
            }
        }
        
        return hunks
    }
    
    /**
     * Parse hunk header components.
     * Format: @@ -oldStart[,oldCount] +newStart[,newCount] @@
     * If count is omitted, it defaults to 1.
     */
    private fun parseHunkHeader(match: MatchResult): HunkHeader {
        val oldStart = match.groupValues[1].toInt()
        val oldCount = match.groupValues[2].toIntOrNull() ?: 1
        val newStart = match.groupValues[3].toInt()
        val newCount = match.groupValues[4].toIntOrNull() ?: 1
        
        return HunkHeader(oldStart, oldCount, newStart, newCount)
    }
    
    /**
     * Internal data class for hunk header components.
     */
    private data class HunkHeader(
        val oldStart: Int,
        val oldCount: Int,
        val newStart: Int,
        val newCount: Int
    )
}
