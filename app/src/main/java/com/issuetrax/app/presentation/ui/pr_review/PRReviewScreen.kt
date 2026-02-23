package com.issuetrax.app.presentation.ui.pr_review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.issuetrax.app.domain.entity.PullRequest
import com.issuetrax.app.presentation.ui.common.components.ErrorText
import com.issuetrax.app.presentation.ui.common.gesture.GestureCallbacks
import com.issuetrax.app.presentation.ui.common.gesture.GestureDetectionBox
import com.issuetrax.app.presentation.ui.common.markdown.MarkdownText
import com.issuetrax.app.presentation.ui.pr_review.components.FileListView
import com.issuetrax.app.presentation.ui.pr_review.components.HunkDetailView
import com.issuetrax.app.presentation.ui.pr_review.components.InlineDiffView
import com.issuetrax.app.presentation.ui.pr_review.components.PRActionToolbar
import com.issuetrax.app.presentation.ui.pr_review.components.PRMetadataCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PRReviewScreen(
    owner: String,
    repo: String,
    prNumber: Int,
    onNavigateBack: () -> Unit,
    viewModel: PRReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showFileReview by rememberSaveable { mutableStateOf(false) }
    
    // Load pull request data when screen launches
    LaunchedEffect(owner, repo, prNumber) {
        viewModel.loadPullRequest(owner, repo, prNumber)
        viewModel.loadCommitStatus(owner, repo)
        viewModel.loadWorkflowRuns(owner, repo)
    }
    
    // Show action message in snackbar
    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearActionMessage()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.pullRequest?.title ?: "$owner/$repo #$prNumber",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        Text(
                            text = "$owner/$repo #$prNumber",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showFileReview = !showFileReview
                            if (showFileReview) {
                                viewModel.navigateToFileList()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = if (showFileReview) "Back to repo review" else "Open file review"
                        )
                    }

                    // PR Action Toolbar
                    uiState.pullRequest?.let { pr ->
                        PRActionToolbar(
                            pullRequest = pr,
                            onApprove = { viewModel.approvePullRequest(owner, repo, prNumber) },
                            onClose = { viewModel.closePullRequest(owner, repo, prNumber) },
                            onMerge = { mergeMethod, commitTitle, commitMessage ->
                                viewModel.mergePullRequest(owner, repo, prNumber, commitTitle, commitMessage, mergeMethod)
                            },
                            onComment = { body ->
                                viewModel.createComment(owner, repo, prNumber, body)
                            },
                            onWorkflowAction = {
                                viewModel.triggerWorkflowAction(owner, repo)
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ErrorText(
                            text = uiState.error ?: "Unknown error",
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(onClick = { viewModel.loadPullRequest(owner, repo, prNumber) }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.pullRequest != null -> {
                    val pullRequest = uiState.pullRequest ?: return@Box
                    if (!showFileReview) {
                        RepoReviewContent(
                            pullRequest = pullRequest,
                            onRequestHighLevelReview = {
                                viewModel.requestHighLevelReview(owner, repo, prNumber)
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        )
                    } else {
                        when (uiState.viewMode) {
                            PRViewMode.FILE_LIST -> {
                                // Show file list view
                                FileListView(
                                    files = uiState.files,
                                    currentFileIndex = uiState.currentFileIndex,
                                    onFileClick = { index -> viewModel.navigateToFile(index) },
                                    commitStatus = uiState.commitStatus,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                )
                            }
                            PRViewMode.FILE_DIFF -> {
                                // Show file diff with swipe to return
                                uiState.currentFile?.let { currentFile ->
                                    GestureDetectionBox(
                                        callbacks = GestureCallbacks(
                                            onSwipeRight = { viewModel.navigateToFileList() }
                                        ),
                                        enabled = true,
                                        showVisualFeedback = false,
                                        enableHapticFeedback = true,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        InlineDiffView(
                                            fileDiff = currentFile,
                                            onHunkClick = { hunk, index ->
                                                viewModel.selectHunk(hunk, index)
                                            },
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }
                            PRViewMode.HUNK_DETAIL -> {
                                // Show full-screen hunk detail with close button
                                uiState.selectedHunk?.let { hunk ->
                                    HunkDetailView(
                                        hunk = hunk,
                                        hunkIndex = uiState.selectedHunkIndex,
                                        fileName = uiState.currentFile?.filename ?: "",
                                        onClose = { viewModel.closeHunkDetail() },
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoReviewContent(
    pullRequest: PullRequest,
    onRequestHighLevelReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PRMetadataCard(pullRequest = pullRequest)
        Button(
            onClick = onRequestHighLevelReview,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Request High-Level Review")
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "PR Description",
                    style = MaterialTheme.typography.titleMedium
                )
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
        }
    }
}
