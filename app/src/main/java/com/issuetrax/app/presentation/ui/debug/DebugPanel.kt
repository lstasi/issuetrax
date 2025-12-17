package com.issuetrax.app.presentation.ui.debug

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.issuetrax.app.domain.debug.HttpRequestInfo
import java.text.SimpleDateFormat
import java.util.*

/**
 * Debug panel that displays HTTP requests at the bottom of the screen.
 * Only visible in debug builds.
 */
@Composable
fun DebugPanel(
    modifier: Modifier = Modifier,
    viewModel: DebugPanelViewModel = hiltViewModel()
) {
    val requests by viewModel.requests.collectAsState()
    val latestRequest = requests.firstOrNull()
    
    var isExpanded by remember { mutableStateOf(false) }
    var selectedRequest by remember { mutableStateOf<HttpRequestInfo?>(null) }
    
    // Show panel if there are requests
    if (latestRequest != null) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            // Collapsed view - shows latest request summary
            if (!isExpanded) {
                LatestRequestBar(
                    request = latestRequest,
                    onExpand = { isExpanded = true },
                    onClear = { viewModel.clearRequests() }
                )
            }
            
            // Expanded view - shows all requests
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                ExpandedDebugPanel(
                    requests = requests,
                    selectedRequest = selectedRequest,
                    onRequestSelected = { selectedRequest = it },
                    onCollapse = { 
                        isExpanded = false
                        selectedRequest = null
                    },
                    onClear = { 
                        viewModel.clearRequests()
                        isExpanded = false
                        selectedRequest = null
                    }
                )
            }
        }
    }
}

@Composable
private fun LatestRequestBar(
    request: HttpRequestInfo,
    onExpand: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpand),
        color = when {
            request.error != null -> MaterialTheme.colorScheme.errorContainer
            request.isSuccess -> Color(0xFF4CAF50).copy(alpha = 0.2f)
            else -> Color(0xFFFF9800).copy(alpha = 0.2f)
        },
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = request.method,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = request.displayUrl,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = request.statusSummary,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    request.responseCode?.let { code ->
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• $code",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    request.durationMs?.let { duration ->
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• ${duration}ms",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            Row {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                IconButton(
                    onClick = onExpand,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpandedDebugPanel(
    requests: List<HttpRequestInfo>,
    selectedRequest: HttpRequestInfo?,
    onRequestSelected: (HttpRequestInfo?) -> Unit,
    onCollapse: () -> Unit,
    onClear: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HTTP Requests (${requests.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear all"
                        )
                    }
                    IconButton(onClick = onCollapse) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Collapse"
                        )
                    }
                }
            }
            
            if (selectedRequest != null) {
                // Show request details
                RequestDetails(
                    request = selectedRequest,
                    onBack = { onRequestSelected(null) }
                )
            } else {
                // Show request list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(requests, key = { it.id }) { request ->
                        RequestItem(
                            request = request,
                            onClick = { onRequestSelected(request) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestItem(
    request: HttpRequestInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                request.error != null -> MaterialTheme.colorScheme.errorContainer
                request.isSuccess -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                else -> Color(0xFFFF9800).copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = request.method,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    request.responseCode?.let { code ->
                        Text(
                            text = code.toString(),
                            fontSize = 12.sp,
                            color = if (request.isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                request.durationMs?.let { duration ->
                    Text(
                        text = "${duration}ms",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = request.displayUrl,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2
            )
            request.error?.let { error ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Error: $error",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.error,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatTimestamp(request.timestamp),
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun RequestDetails(
    request: HttpRequestInfo,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onBack)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back to list",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Request summary
                Text(
                    text = "${request.method} ${request.displayUrl}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row {
                    request.responseCode?.let { code ->
                        Chip(text = "Status: $code")
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    request.durationMs?.let { duration ->
                        Chip(text = "${duration}ms")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Request headers
                SectionHeader("Request Headers")
                CodeBlock(
                    text = request.requestHeaders.entries.joinToString("\n") { 
                        "${it.key}: ${it.value}" 
                    }
                )
                
                // Request body
                request.requestBody?.let { body ->
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader("Request Body")
                    CodeBlock(text = body)
                }
                
                // Response headers
                request.responseHeaders?.let { headers ->
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader("Response Headers")
                    CodeBlock(
                        text = headers.entries.joinToString("\n") { 
                            "${it.key}: ${it.value}" 
                        }
                    )
                }
                
                // Response body
                request.responseBody?.let { body ->
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader("Response Body")
                    CodeBlock(text = body)
                }
                
                // Error
                request.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader("Error")
                    Text(
                        text = error,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun CodeBlock(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Chip(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
