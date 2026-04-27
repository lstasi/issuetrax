package com.issuetrax.app.presentation.ui.pr_review.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.issuetrax.app.domain.usecase.ReviewEvent

/**
 * Dialog for submitting a formal GitHub pull request review.
 *
 * Allows the user to choose a review type (Approve / Request Changes / Comment)
 * and optionally provide a review body before submitting.
 *
 * @param prNumber Pull request number displayed in the dialog title.
 * @param isSubmitting When true, action buttons are disabled and a progress indicator is shown.
 * @param onDismiss Called when the user cancels or dismisses the dialog.
 * @param onSubmit Called when the user confirms submission. Receives the chosen [ReviewEvent]
 *   and an optional review body string.
 */
@Composable
fun ReviewSubmissionDialog(
    prNumber: Int,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (event: ReviewEvent, body: String?) -> Unit,
) {
    var selectedEvent by remember { mutableStateOf(ReviewEvent.COMMENT) }
    var reviewBody by remember { mutableStateOf("") }

    val bodyRequired = selectedEvent == ReviewEvent.REQUEST_CHANGES
    val submitEnabled = !isSubmitting && (!bodyRequired || reviewBody.isNotBlank())

    AlertDialog(
        onDismissRequest = { if (!isSubmitting) onDismiss() },
        icon = {
            Icon(
                imageVector = Icons.Default.RateReview,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        },
        title = {
            Text("Submit Review for PR #$prNumber")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Choose a review type:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                ReviewEventOption(
                    label = "Comment",
                    description = "Submit general feedback without explicit approval.",
                    selected = selectedEvent == ReviewEvent.COMMENT,
                    onClick = { selectedEvent = ReviewEvent.COMMENT },
                )

                ReviewEventOption(
                    label = "Approve",
                    description = "Submit feedback and approve the changes.",
                    selected = selectedEvent == ReviewEvent.APPROVE,
                    onClick = { selectedEvent = ReviewEvent.APPROVE },
                )

                ReviewEventOption(
                    label = "Request changes",
                    description = "Submit feedback that must be addressed before merging.",
                    selected = selectedEvent == ReviewEvent.REQUEST_CHANGES,
                    onClick = { selectedEvent = ReviewEvent.REQUEST_CHANGES },
                )

                OutlinedTextField(
                    value = reviewBody,
                    onValueChange = { reviewBody = it },
                    label = {
                        Text(
                            if (bodyRequired) "Review comment (required)" else "Review comment (optional)",
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6,
                    placeholder = { Text("Leave a comment...") },
                    isError = bodyRequired && reviewBody.isBlank(),
                    enabled = !isSubmitting,
                )

                if (isSubmitting) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(selectedEvent, reviewBody.ifBlank { null })
                },
                enabled = submitEnabled,
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isSubmitting,
            ) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun ReviewEventOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
        Column(modifier = Modifier.padding(start = 4.dp, top = 12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
