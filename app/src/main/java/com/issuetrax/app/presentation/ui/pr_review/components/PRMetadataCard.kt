package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.presentation.ui.current_work.PRStateIndicator
import com.issuetrax.app.presentation.ui.current_work.PRStats
import com.issuetrax.app.presentation.ui.current_work.timeAgo

/**
 * Displays compact metadata for a pull request.
 * 
 * Simplified version showing only:
 * - PR state (open/closed/merged)
 * - Author information
 * - Branch information (head → base)
 * - Stats (commits, files changed, additions, deletions)
 * - Timestamps
 * 
 * Note: PR title is now shown in the top toolbar, and description is available via the info button.
 */
@Composable
fun PRMetadataCard(
    pullRequest: PullRequest,
    modifier: Modifier = Modifier,
    compact: Boolean = true
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
            // Header: PR number and state
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#${pullRequest.number}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "by ${pullRequest.author.login}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                PRStateIndicator(state = pullRequest.state)
            }
            
            // Branch information
            BranchInfo(
                headRef = pullRequest.headRef,
                baseRef = pullRequest.baseRef
            )
            
            // Stats and timestamps in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stats
                PRStats(
                    changedFiles = pullRequest.changedFiles ?: 0,
                    additions = pullRequest.additions ?: 0,
                    deletions = pullRequest.deletions ?: 0
                )
                
                // Updated timestamp
                Text(
                    text = "Updated ${timeAgo(pullRequest.updatedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Displays branch information (head → base).
 */
@Composable
private fun BranchInfo(
    headRef: String,
    baseRef: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = headRef,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        
        Text(
            text = "→",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = baseRef,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

/**
 * Displays timestamp information (created, updated, merged, closed).
 * 
 * Note: This is now only used in non-compact mode (future feature).
 */
@Composable
private fun TimeInfo(
    createdAt: String,
    updatedAt: String,
    mergedAt: String?,
    closedAt: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Created $createdAt",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = "Updated $updatedAt",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        mergedAt?.let {
            Text(
                text = "Merged $it",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        
        closedAt?.let {
            if (mergedAt == null) { // Only show closed if not merged
                Text(
                    text = "Closed $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
