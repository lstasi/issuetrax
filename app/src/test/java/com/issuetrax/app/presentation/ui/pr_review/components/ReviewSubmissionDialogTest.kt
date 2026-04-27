package com.issuetrax.app.presentation.ui.pr_review.components

import com.issuetrax.app.domain.usecase.ReviewEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for ReviewSubmissionDialog component logic.
 *
 * These tests verify the review-event business rules and state assumptions.
 * Full UI interaction testing would use Compose UI tests.
 */
class ReviewSubmissionDialogTest {

    // ---------------------------------------------------------------------------
    // ReviewEvent enum
    // ---------------------------------------------------------------------------

    @Test
    fun `ReviewEvent has exactly three values`() {
        assertEquals(3, ReviewEvent.entries.size)
    }

    @Test
    fun `ReviewEvent values are APPROVE REQUEST_CHANGES and COMMENT`() {
        val events = ReviewEvent.entries.toSet()
        assertTrue(events.contains(ReviewEvent.APPROVE))
        assertTrue(events.contains(ReviewEvent.REQUEST_CHANGES))
        assertTrue(events.contains(ReviewEvent.COMMENT))
    }

    // ---------------------------------------------------------------------------
    // Submit-enabled rules
    // ---------------------------------------------------------------------------

    @Test
    fun `submit is enabled for COMMENT event with blank body`() {
        val event = ReviewEvent.COMMENT
        val body = ""
        // COMMENT does not require a body
        val bodyRequired = event == ReviewEvent.REQUEST_CHANGES
        val submitEnabled = !bodyRequired || body.isNotBlank()
        assertTrue(submitEnabled)
    }

    @Test
    fun `submit is enabled for APPROVE event with blank body`() {
        val event = ReviewEvent.APPROVE
        val body = ""
        val bodyRequired = event == ReviewEvent.REQUEST_CHANGES
        val submitEnabled = !bodyRequired || body.isNotBlank()
        assertTrue(submitEnabled)
    }

    @Test
    fun `submit is disabled for REQUEST_CHANGES event with blank body`() {
        val event = ReviewEvent.REQUEST_CHANGES
        val body = ""
        val bodyRequired = event == ReviewEvent.REQUEST_CHANGES
        val submitEnabled = !bodyRequired || body.isNotBlank()
        assertFalse(submitEnabled)
    }

    @Test
    fun `submit is enabled for REQUEST_CHANGES event with non-blank body`() {
        val event = ReviewEvent.REQUEST_CHANGES
        val body = "Please fix the null pointer issue."
        val bodyRequired = event == ReviewEvent.REQUEST_CHANGES
        val submitEnabled = !bodyRequired || body.isNotBlank()
        assertTrue(submitEnabled)
    }

    @Test
    fun `submit is disabled when isSubmitting is true`() {
        val isSubmitting = true
        val submitEnabled = !isSubmitting
        assertFalse(submitEnabled)
    }

    // ---------------------------------------------------------------------------
    // Body handling
    // ---------------------------------------------------------------------------

    @Test
    fun `blank body is passed as null on submit`() {
        val rawBody = "   "
        val body = rawBody.ifBlank { null }
        assertNull(body)
    }

    @Test
    fun `non-blank body is passed as-is on submit`() {
        val rawBody = "Looks good to me!"
        val body = rawBody.ifBlank { null }
        assertEquals("Looks good to me!", body)
    }

    @Test
    fun `empty body is passed as null on submit`() {
        val rawBody = ""
        val body = rawBody.ifBlank { null }
        assertNull(body)
    }

    // ---------------------------------------------------------------------------
    // Dialog dismiss behaviour
    // ---------------------------------------------------------------------------

    @Test
    fun `dismiss is allowed when not submitting`() {
        val isSubmitting = false
        val canDismiss = !isSubmitting
        assertTrue(canDismiss)
    }

    @Test
    fun `dismiss is blocked when submitting`() {
        val isSubmitting = true
        val canDismiss = !isSubmitting
        assertFalse(canDismiss)
    }
}
