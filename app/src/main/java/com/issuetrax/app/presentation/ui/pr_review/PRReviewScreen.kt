package com.issuetrax.app.presentation.ui.pr_review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.issuetrax.app.presentation.ui.common.components.ErrorText
import com.issuetrax.app.presentation.ui.common.gesture.GestureCallbacks
import com.issuetrax.app.presentation.ui.common.gesture.GestureDetectionBox
import com.issuetrax.app.presentation.ui.pr_review.components.FileListView
import com.issuetrax.app.presentation.ui.pr_review.components.HunkDetailView
import com.issuetrax.app.presentation.ui.pr_review.components.InlineDiffView
import com.issuetrax.app.presentation.ui.pr_review.components.PRActionToolbar
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
    
    // Load pull request data when screen launches
    LaunchedEffect(owner, repo, prNumber) {
        viewModel.loadPullRequest(owner, repo, prNumber)
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
                    // PR Action Toolbar
                    uiState.pullRequest?.let { pr ->
                        PRActionToolbar(
                            pullRequest = pr,
                            onApprove = { viewModel.approvePullRequest(owner, repo, prNumber) },
                            onClose = { viewModel.closePullRequest(owner, repo, prNumber) }
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
                    when (uiState.viewMode) {
                        PRViewMode.FILE_LIST -> {
                            // Show file list view
                            FileListView(
                                files = uiState.files,
                                currentFileIndex = uiState.currentFileIndex,
                                onFileClick = { index -> viewModel.navigateToFile(index) },
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
                                    showVisualFeedback = true,
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