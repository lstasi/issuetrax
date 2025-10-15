package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.issuetrax.app.domain.entity.CodeHunk

/**
 * Displays a code hunk from a diff with header and all lines.
 * 
 * A hunk represents a contiguous block of changes in a file.
 * The header shows the line number ranges for old and new versions.
 * 
 * Format: @@ -oldStart,oldCount +newStart,newCount @@
 * 
 * @param hunk The code hunk to display
 * @param modifier Modifier to be applied to the column
 */
@Composable
fun DiffHunk(
    hunk: CodeHunk,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Hunk header
        Text(
            text = "@@ -${hunk.oldStart},${hunk.oldCount} +${hunk.newStart},${hunk.newCount} @@",
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Diff lines
        hunk.lines.forEach { line ->
            DiffLine(line = line)
        }
    }
}
