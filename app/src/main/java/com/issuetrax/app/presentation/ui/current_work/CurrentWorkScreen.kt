package com.issuetrax.app.presentation.ui.current_work

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.issuetrax.app.R
import com.issuetrax.app.domain.entity.PRState
import com.issuetrax.app.domain.entity.PullRequest
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWorkScreen(
    owner: String,
    repo: String,
    onNavigateToPR: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CurrentWorkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterMenu by remember { mutableStateOf(false) }

    // Load pull requests on initial composition
    LaunchedEffect(owner, repo) {
        viewModel.loadPullRequests(owner, repo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$owner/$repo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshPullRequests() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    Box {
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Filter")
                        }
                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.current_work_filter_all)) },
                                onClick = {
                                    viewModel.filterPullRequests(PRFilter.ALL)
                                    showFilterMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.current_work_filter_open)) },
                                onClick = {
                                    viewModel.filterPullRequests(PRFilter.OPEN)
                                    showFilterMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.current_work_filter_closed)) },
                                onClick = {
                                    viewModel.filterPullRequests(PRFilter.CLOSED)
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoadingPRs && uiState.pullRequests.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshPullRequests() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }

                uiState.pullRequests.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.current_work_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.pullRequests) { pullRequest ->
                            PullRequestItem(
                                pullRequest = pullRequest,
                                onClick = { onNavigateToPR(pullRequest.number) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PullRequestItem(
    pullRequest: PullRequest,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${pullRequest.number}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.2f)
                )

                PRStateIndicator(
                    state = pullRequest.state,
                    modifier = Modifier.weight(0.3f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pullRequest.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "by ${pullRequest.author.login}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            PRStats(
                changedFiles = pullRequest.changedFiles ?: 0,
                additions = pullRequest.additions ?: 0,
                deletions = pullRequest.deletions ?: 0
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = timeAgo(pullRequest.updatedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PRStateIndicator(
    state: PRState,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (state) {
        PRState.OPEN -> stringResource(R.string.current_work_state_open) to MaterialTheme.colorScheme.primary
        PRState.CLOSED -> stringResource(R.string.current_work_state_closed) to MaterialTheme.colorScheme.error
        PRState.MERGED -> stringResource(R.string.current_work_state_merged) to MaterialTheme.colorScheme.tertiary
    }

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
    )
}

@Composable
fun PRStats(
    changedFiles: Int,
    additions: Int,
    deletions: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "$changedFiles ${if (changedFiles == 1) "file" else "files"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "+$additions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "-$deletions",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

fun timeAgo(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(dateTime, now)

    return when {
        duration.toDays() > 365 -> "${duration.toDays() / 365} ${if (duration.toDays() / 365 == 1L) "year" else "years"} ago"
        duration.toDays() > 30 -> "${duration.toDays() / 30} ${if (duration.toDays() / 30 == 1L) "month" else "months"} ago"
        duration.toDays() > 0 -> "${duration.toDays()} ${if (duration.toDays() == 1L) "day" else "days"} ago"
        duration.toHours() > 0 -> "${duration.toHours()} ${if (duration.toHours() == 1L) "hour" else "hours"} ago"
        duration.toMinutes() > 0 -> "${duration.toMinutes()} ${if (duration.toMinutes() == 1L) "minute" else "minutes"} ago"
        else -> "just now"
    }
}
