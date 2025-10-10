package com.issuetrax.app.presentation.ui.common.components

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

/**
 * A reusable composable for displaying selectable error messages.
 * 
 * Error messages are wrapped in SelectionContainer to allow users to select and copy
 * the text for bug reports and troubleshooting.
 * 
 * @param text The error message to display
 * @param modifier Modifier to be applied to the text
 * @param style Text style (defaults to bodyLarge)
 * @param color Text color (defaults to error color from theme)
 * @param textAlign Text alignment (defaults to center)
 */
@Composable
fun ErrorText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.error,
    textAlign: TextAlign = TextAlign.Center
) {
    SelectionContainer {
        Text(
            text = text,
            modifier = modifier,
            style = style,
            color = color,
            textAlign = textAlign
        )
    }
}
