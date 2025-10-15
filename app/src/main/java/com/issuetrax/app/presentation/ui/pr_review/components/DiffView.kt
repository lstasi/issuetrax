package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.util.DiffParser

/**
 * Displays a complete file diff with file info and all code hunks.
 * 
 * Shows:
 * - File name with full path
 * - Addition/deletion statistics
 * - All code hunks parsed from the patch
 * 
 * The patch is parsed using DiffParser to extract hunks, which are then
 * rendered using DiffHunk composables.
 * 
 * If the file has no patch (binary files, removed files without content),
 * only the file info is displayed.
 * 
 * @param fileDiff The file diff to display
 * @param modifier Modifier to be applied to the card
 */
@Composable
fun DiffView(
    fileDiff: FileDiff,
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
                    hunks.forEach { hunk ->
                        DiffHunk(hunk = hunk)
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
