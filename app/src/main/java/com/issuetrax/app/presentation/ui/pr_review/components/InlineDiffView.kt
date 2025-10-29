package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.issuetrax.app.domain.entity.CodeHunk
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.util.DiffParser

/**
 * Configuration constants for inline diff view behavior.
 * These can be adjusted to customize the expand/collapse behavior.
 */
const val COLLAPSE_THRESHOLD_LINES = 10
const val PREVIEW_LINES_WHEN_COLLAPSED = 5

/**
 * Inline diff view optimized for mobile screens.
 * 
 * Shows old and new lines together in a compact format:
 * - Deletions and additions are paired and shown together
 * - Context lines are shown once (not duplicated)
 * - Line numbers are optimized for space
 * - Large hunks can be collapsed/expanded
 * - Hunks can be clicked to view in full-screen detail
 * 
 * This view is designed for mobile screen widths where horizontal space is limited.
 * 
 * @param fileDiff The file diff to display
 * @param onHunkClick Callback when a hunk is clicked for full-screen view
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun InlineDiffView(
    fileDiff: FileDiff,
    onHunkClick: ((CodeHunk, Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // File header with name and stats
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = fileDiff.filename,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (fileDiff.additions > 0) {
                        Text(
                            text = "+${fileDiff.additions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (fileDiff.deletions > 0) {
                        Text(
                            text = "-${fileDiff.deletions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Parse and display hunks
            val hunks = DiffParser.parse(fileDiff.patch)
            if (hunks.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hunks.forEachIndexed { index, hunk ->
                        InlineDiffHunk(
                            hunk = hunk,
                            hunkIndex = index + 1,
                            totalHunks = hunks.size,
                            onHunkClick = onHunkClick?.let { callback ->
                                { callback(hunk, index + 1) }
                            }
                        )
                    }
                }
            } else if (fileDiff.patch != null) {
                // Patch exists but couldn't be parsed (binary file, etc.)
                Text(
                    text = "Binary file or no displayable changes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/**
 * Displays a single hunk in inline format with expand/collapse capability.
 * 
 * Large hunks (more than [COLLAPSE_THRESHOLD_LINES] lines) can be collapsed to save screen space.
 * When collapsed, shows first [PREVIEW_LINES_WHEN_COLLAPSED] lines.
 * The hunk header shows the line ranges and hunk number.
 * Clicking on the hunk opens it in full-screen detail view.
 * 
 * @param hunk The code hunk to display
 * @param hunkIndex The 1-based index of this hunk
 * @param totalHunks The total number of hunks in the file
 * @param onHunkClick Callback when the hunk is clicked for full-screen view
 * @param modifier Modifier to be applied to the column
 */
@Composable
fun InlineDiffHunk(
    hunk: CodeHunk,
    hunkIndex: Int,
    totalHunks: Int,
    onHunkClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(hunk.lines.size <= COLLAPSE_THRESHOLD_LINES) }
    val shouldShowToggle = hunk.lines.size > COLLAPSE_THRESHOLD_LINES
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onHunkClick != null) {
                    Modifier.clickable { onHunkClick() }
                } else {
                    Modifier
                }
            )
    ) {
        // Hunk header with expand/collapse button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hunk $hunkIndex/$totalHunks: @@ -${hunk.oldStart},${hunk.oldCount} +${hunk.newStart},${hunk.newCount} @@",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            
            if (shouldShowToggle) {
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.padding(0.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Show diff lines (all if expanded, or preview lines if collapsed)
        if (isExpanded) {
            hunk.lines.forEach { line ->
                DiffLine(line = line)
            }
        } else {
            // Show first preview lines when collapsed
            hunk.lines.take(PREVIEW_LINES_WHEN_COLLAPSED).forEach { line ->
                DiffLine(line = line)
            }
            
            // Show count of hidden lines
            if (hunk.lines.size > PREVIEW_LINES_WHEN_COLLAPSED) {
                Text(
                    text = "... ${hunk.lines.size - PREVIEW_LINES_WHEN_COLLAPSED} more lines",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
