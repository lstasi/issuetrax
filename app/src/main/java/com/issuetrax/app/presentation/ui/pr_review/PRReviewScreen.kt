package com.issuetrax.app.presentation.ui.pr_review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.issuetrax.app.R
import com.issuetrax.app.presentation.ui.common.components.ErrorText
import com.issuetrax.app.presentation.ui.pr_review.components.PRMetadataCard

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
    
    // Load pull request data when screen launches
    LaunchedEffect(owner, repo, prNumber) {
        viewModel.loadPullRequest(owner, repo, prNumber)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("$owner/$repo #$prNumber")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                    uiState.pullRequest?.let { pullRequest ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // PR Metadata Card
                            PRMetadataCard(pullRequest = pullRequest)
                        
                        // Placeholder for future components (file list, diff viewer, etc.)
                        Text(
                            text = "Files loaded: ${uiState.files.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "File list and diff viewer will be implemented next.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        }
                    }
                }
            }
        }
    }
}