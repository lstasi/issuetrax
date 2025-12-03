package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.issuetrax.app.domain.entity.FileDiff
import com.issuetrax.app.domain.entity.FileStatus

/**
 * Displays a list of changed files in a pull request.
 * 
 * Shows:
 * - File counter (e.g., "File 1 of 12")
 * - Build status indicator
 * - List of files with:
 *   - File name with full path
 *   - Change type (added/modified/removed/renamed) with icon
 *   - Addition/deletion counts
 * - Highlights the currently selected file
 * - Click handler to navigate to specific file
 */
@Composable
fun FileListView(
    files: List<FileDiff>,
    currentFileIndex: Int,
    onFileClick: (Int) -> Unit,
    commitStatus: com.issuetrax.app.domain.entity.CommitStatus? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // File counter and build status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (files.isNotEmpty()) {
                Text(
                    text = if (currentFileIndex >= 0) {
                        "File ${currentFileIndex + 1} of ${files.size}"
                    } else {
                        "${files.size} ${if (files.size == 1) "file" else "files"} changed"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Build status indicator
            commitStatus?.let { status ->
                BuildStatusIndicator(status = status)
            }
        }
        
        // File list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(files) { index, file ->
                FileItem(
                    file = file,
                    isSelected = index == currentFileIndex,
                    onClick = { onFileClick(index) }
                )
            }
        }
    }
}

/**
 * Displays a single file item with status, path, and change statistics.
 */
@Composable
private fun FileItem(
    file: FileDiff,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // File status icon and filename
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FileStatusIcon(status = file.status)
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = file.filename,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Show previous filename for renamed files
                    if (file.status == FileStatus.RENAMED && file.previousFilename != null) {
                        Text(
                            text = "from ${file.previousFilename}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            

        }
    }
}

/**
 * Displays an icon representing the file change status.
 */
@Composable
private fun FileStatusIcon(
    status: FileStatus,
    modifier: Modifier = Modifier
) {
    val (icon, backgroundColor, iconColor) = when (status) {
        FileStatus.ADDED -> Triple(
            Icons.Default.Add,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        FileStatus.MODIFIED, FileStatus.CHANGED -> Triple(
            Icons.Default.Edit,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary
        )
        FileStatus.REMOVED -> Triple(
            Icons.Filled.Delete,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        )
        FileStatus.RENAMED -> Triple(
            Icons.Filled.DriveFileRenameOutline,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )
        FileStatus.COPIED -> Triple(
            Icons.Filled.ContentCopy,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )
        FileStatus.UNCHANGED -> Triple(
            Icons.Default.Edit,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Icon(
        imageVector = icon,
        contentDescription = status.name,
        modifier = modifier
            .size(32.dp)
            .background(backgroundColor, CircleShape)
            .padding(6.dp),
        tint = iconColor
    )
}

/**
 * Displays a text label for the file status.
 */
@Composable
private fun FileStatusLabel(
    status: FileStatus,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        FileStatus.ADDED -> "added" to MaterialTheme.colorScheme.primary
        FileStatus.MODIFIED -> "modified" to MaterialTheme.colorScheme.tertiary
        FileStatus.REMOVED -> "removed" to MaterialTheme.colorScheme.error
        FileStatus.RENAMED -> "renamed" to MaterialTheme.colorScheme.secondary
        FileStatus.COPIED -> "copied" to MaterialTheme.colorScheme.secondary
        FileStatus.CHANGED -> "changed" to MaterialTheme.colorScheme.tertiary
        FileStatus.UNCHANGED -> "unchanged" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
    )
}

/**
 * Displays a build status indicator showing the overall commit status.
 */
@Composable
private fun BuildStatusIndicator(
    status: com.issuetrax.app.domain.entity.CommitStatus,
    modifier: Modifier = Modifier
) {
    data class StatusDisplay(
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val backgroundColor: androidx.compose.ui.graphics.Color,
        val iconColor: androidx.compose.ui.graphics.Color,
        val text: String
    )
    
    val display = when (status.state) {
        com.issuetrax.app.domain.entity.CommitState.SUCCESS -> StatusDisplay(
            icon = Icons.Default.CheckCircle,
            backgroundColor = MaterialTheme.colorScheme.primary,
            iconColor = MaterialTheme.colorScheme.onPrimary,
            text = "Checks passed"
        )
        com.issuetrax.app.domain.entity.CommitState.FAILURE -> StatusDisplay(
            icon = Icons.Filled.Error,
            backgroundColor = MaterialTheme.colorScheme.error,
            iconColor = MaterialTheme.colorScheme.onError,
            text = "Checks failed"
        )
        com.issuetrax.app.domain.entity.CommitState.PENDING -> StatusDisplay(
            icon = Icons.Default.Refresh,
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            iconColor = MaterialTheme.colorScheme.onTertiary,
            text = "Checks pending"
        )
        com.issuetrax.app.domain.entity.CommitState.ERROR -> StatusDisplay(
            icon = Icons.Filled.Error,
            backgroundColor = MaterialTheme.colorScheme.error,
            iconColor = MaterialTheme.colorScheme.onError,
            text = "Checks error"
        )
    }
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = display.icon,
            contentDescription = display.text,
            modifier = Modifier
                .size(20.dp)
                .background(display.backgroundColor, CircleShape)
                .padding(3.dp),
            tint = display.iconColor
        )
        Text(
            text = display.text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
