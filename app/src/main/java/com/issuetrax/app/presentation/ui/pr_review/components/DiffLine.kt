package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.issuetrax.app.domain.entity.DiffLine
import com.issuetrax.app.domain.entity.LineType
import com.issuetrax.app.presentation.ui.common.theme.DiffAdded
import com.issuetrax.app.presentation.ui.common.theme.DiffRemoved

/**
 * Displays a single line in a code diff with line numbers and color coding.
 * 
 * Color scheme:
 * - ADDITION: Green background for added lines
 * - DELETION: Red background for deleted lines
 * - CONTEXT: Default surface color for context lines
 * - NO_NEWLINE: Gray text for special markers
 * 
 * Layout:
 * - Old line number (left column, 4 chars wide)
 * - New line number (middle column, 4 chars wide)
 * - Code content (right column, monospace font)
 * - Horizontal scroll for long lines
 * 
 * @param line The diff line to display
 * @param modifier Modifier to be applied to the row
 */
@Composable
fun DiffLine(
    line: DiffLine,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (line.type) {
        LineType.ADDITION -> DiffAdded
        LineType.DELETION -> DiffRemoved
        LineType.CONTEXT -> MaterialTheme.colorScheme.surface
        LineType.NO_NEWLINE -> MaterialTheme.colorScheme.surface
    }
    
    val textColor = when (line.type) {
        LineType.NO_NEWLINE -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Row(
        modifier = modifier
            .background(backgroundColor)
            .horizontalScroll(rememberScrollState())
    ) {
        // Old line number (left gutter)
        Text(
            text = line.oldLineNumber?.toString() ?: "",
            modifier = Modifier
                .width(40.dp)
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
        
        // New line number (middle gutter)
        Text(
            text = line.newLineNumber?.toString() ?: "",
            modifier = Modifier
                .width(40.dp)
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End
        )
        
        // Code content
        Text(
            text = line.content,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            ),
            color = textColor,
            maxLines = 1
        )
    }
}
