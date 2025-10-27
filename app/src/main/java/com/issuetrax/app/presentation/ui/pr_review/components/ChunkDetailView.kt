package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.util.DiffParser

/**
 * Displays a single chunk in full screen with a close button.
 * 
 * @param fileDiff The file diff containing the chunk
 * @param chunkIndex The index of the chunk to display
 * @param onClose Callback when close button is clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChunkDetailView(
    fileDiff: FileDiff,
    chunkIndex: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hunks = DiffParser.parse(fileDiff.patch)
    
    if (chunkIndex < 0 || chunkIndex >= hunks.size) {
        // Invalid chunk index
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Invalid chunk",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }
    
    val chunk = hunks[chunkIndex]
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top bar with close button
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Chunk ${chunkIndex + 1} of ${hunks.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = fileDiff.filename,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            actions = {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }
        )
        
        // Chunk content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                DiffHunk(
                    hunk = chunk,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
