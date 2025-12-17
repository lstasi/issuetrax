package com.issuetrax.app.presentation.ui.current_work

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.issuetrax.app.domain.entity.WorkflowRun

/**
 * A bottom sheet that displays GitHub Actions workflow runs for a specific PR.
 * 
 * @param workflowRuns The list of workflow runs to display
 * @param isLoading Whether workflow runs are being loaded
 * @param onDismiss Callback when the sheet is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildActionsSheet(
    workflowRuns: List<WorkflowRun>,
    isLoading: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Text(
                text = "GitHub Actions Builds",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                workflowRuns.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No workflow runs found for this PR",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(workflowRuns) { workflowRun ->
                            WorkflowRunItem(workflowRun = workflowRun)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Displays a single workflow run item.
 */
@Composable
fun WorkflowRunItem(
    workflowRun: WorkflowRun
) {
    val uriHandler = LocalUriHandler.current
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Workflow name
            Text(
                text = workflowRun.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Status and conclusion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status indicator
                    WorkflowStatusBadge(
                        status = workflowRun.status,
                        conclusion = workflowRun.conclusion
                    )
                }
                
                // Link to GitHub
                TextButton(
                    onClick = { uriHandler.openUri(workflowRun.htmlUrl) }
                ) {
                    Text("View on GitHub")
                }
            }
        }
    }
}

/**
 * Displays a badge indicating the workflow run status.
 */
@Composable
fun WorkflowStatusBadge(
    status: String,
    conclusion: String?
) {
    val (text, color) = when {
        status == "completed" && conclusion == "success" -> "Success" to Color(0xFF22863A)
        status == "completed" && conclusion == "failure" -> "Failed" to MaterialTheme.colorScheme.error
        status == "completed" && conclusion == "cancelled" -> "Cancelled" to Color(0xFF8B949E)
        status == "completed" && conclusion == "skipped" -> "Skipped" to Color(0xFF8B949E)
        status == "in_progress" -> "In Progress" to Color(0xFFDBB300)
        status == "queued" -> "Queued" to Color(0xFFDBB300)
        status == "waiting" -> "Waiting" to Color(0xFFDBB300)
        else -> status.replaceFirstChar { it.uppercase() } to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Medium
    )
}
