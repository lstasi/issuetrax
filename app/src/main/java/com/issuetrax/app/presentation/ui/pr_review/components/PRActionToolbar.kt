package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextField
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
 * - Merge button: Merge the PR into the base branch
 * - Approve button: Quick approve without detailed review
 * - Comment button: Add a comment starting with @copilot
 * - Close PR button: Close the PR without merging
 * - Run workflow button: Approve workflow runs (for PRs with pending workflows)
 */
@Composable
fun PRActionToolbar(
    pullRequest: PullRequest,
    onApprove: () -> Unit,
    onClose: () -> Unit,
    onMerge: (String, String?, String?) -> Unit,
    onComment: (String) -> Unit,
    onApproveWorkflow: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showDescription by remember { mutableStateOf(false) }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showCloseDialog by remember { mutableStateOf(false) }
    var showMergeDialog by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    
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
        
        // Merge button - only for open PRs
        if (pullRequest.state == PRState.OPEN) {
            IconButton(onClick = { showMergeDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Merge PR",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
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
        
        // Approve workflow button - only for open PRs
        if (pullRequest.state == PRState.OPEN) {
            IconButton(onClick = onApproveWorkflow) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Approve workflow run",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        
        // Comment button - for all PRs
        IconButton(onClick = { showCommentDialog = true }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Add comment",
                tint = MaterialTheme.colorScheme.onSurface
            )
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
    
    // Merge PR Dialog
    if (showMergeDialog) {
        var mergeMethod by remember { mutableStateOf("merge") }
        var commitTitle by remember { mutableStateOf("") }
        var commitMessage by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showMergeDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    onMerge(
                        mergeMethod,
                        commitTitle.ifBlank { null },
                        commitMessage.ifBlank { null }
                    )
                    showMergeDialog = false
                }) {
                    Text("Merge")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMergeDialog = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Merge Pull Request")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PR #${pullRequest.number}: ${pullRequest.title}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    OutlinedTextField(
                        value = commitTitle,
                        onValueChange = { commitTitle = it },
                        label = { Text("Commit title (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = commitMessage,
                        onValueChange = { commitMessage = it },
                        label = { Text("Commit message (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    Text(
                        text = "Merge method: $mergeMethod",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
    
    // Comment Dialog
    if (showCommentDialog) {
        var commentText by remember { mutableStateOf("@copilot ") }
        
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            onComment(commentText)
                            showCommentDialog = false
                        }
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Text("Post Comment")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) {
                    Text("Cancel")
                }
            },
            title = {
                Text("Add Comment to GitHub")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "This will post a comment on PR #${pullRequest.number}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Comment") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 10,
                        placeholder = { Text("Type your comment here...") }
                    )
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}
