package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Navigation buttons for moving between files in a pull request.
 * 
 * Shows:
 * - Previous button (disabled when at first file)
 * - Current file indicator (e.g., "File 2 of 5")
 * - Next button (disabled when at last file)
 */
@Composable
fun FileNavigationButtons(
    currentFileIndex: Int,
    totalFiles: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button
        Button(
            onClick = onPreviousClick,
            enabled = currentFileIndex > 0,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous file"
            )
            Text("Previous")
        }
        
        // Current file indicator
        Text(
            text = "File ${currentFileIndex + 1} of $totalFiles",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        // Next button
        Button(
            onClick = onNextClick,
            enabled = currentFileIndex < totalFiles - 1,
            modifier = Modifier.weight(1f)
        ) {
            Text("Next")
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next file"
            )
        }
    }
}
