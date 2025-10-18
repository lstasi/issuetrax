package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.issuetrax.app.R
import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.presentation.ui.common.markdown.MarkdownText

/**
 * Action toolbar for PR review screen.
 * 
 * Provides quick actions:
 * - Info button: Opens PR description in markdown format
 * - Approve button: Quick approve without detailed review
 * - Close PR button: Close the PR without merging
 * - Run tests button: Trigger CI/CD re-run (future feature)
 * 
 * Note: Merge button is intentionally NOT included to prevent accidental merges.
 * Merging should be done from the web interface after thorough review.
 */
@Composable
fun PRActionToolbar(
    pullRequest: PullRequest,
    onApprove: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDescription by remember { mutableStateOf(false) }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showCloseDialog by remember { mutableStateOf(false) }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Info button - shows PR description
        IconButton(onClick = { showDescription = true }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "View PR description",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // Approve button - only for open PRs
        if (pullRequest.state == PRState.OPEN) {
            IconButton(onClick = { showApproveDialog = true }) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Approve PR",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }
        
        // Close PR button - only for open PRs
        if (pullRequest.state == PRState.OPEN) {
            IconButton(onClick = { showCloseDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close PR",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        
        // Run tests button (future feature - currently disabled)
        // Uncomment when CI/CD integration is implemented
        /*
        IconButton(onClick = { /* TODO: Trigger CI/CD run */ }) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Run tests",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
        */
    }
    
    // PR Description Dialog
    if (showDescription) {
        AlertDialog(
            onDismissRequest = { showDescription = false },
            confirmButton = {
                TextButton(onClick = { showDescription = false }) {
                    Text("Close")
                }
            },
            title = {
                Text(
                    text = pullRequest.title,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                if (!pullRequest.body.isNullOrBlank()) {
                    MarkdownText(markdown = pullRequest.body)
                } else {
                    Text(
                        text = "No description provided.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
    
    // Approve Confirmation Dialog
    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onApprove()
                    showApproveDialog = false
                }) {
                    Text("Approve")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Approve Pull Request")
            },
            text = {
                Text(
                    text = "Are you sure you want to approve this pull request?\n\n" +
                           "PR #${pullRequest.number}: ${pullRequest.title}"
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        )
    }
    
    // Close PR Confirmation Dialog
    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { showCloseDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onClose()
                    showCloseDialog = false
                }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloseDialog = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Close Pull Request")
            },
            text = {
                Text(
                    text = "Are you sure you want to close this pull request without merging?\n\n" +
                           "PR #${pullRequest.number}: ${pullRequest.title}"
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        )
    }
}
