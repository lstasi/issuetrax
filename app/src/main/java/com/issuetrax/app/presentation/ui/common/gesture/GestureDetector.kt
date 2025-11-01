package com.issuetrax.app.presentation.ui.common.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import kotlin.math.abs
import kotlin.math.atan2

/**
 * Gesture detector for PR review navigation following the specifications in UI_UX_DESIGN.md
 * 
 * Detects:
 * - Horizontal swipes (left/right) for file navigation
 * - Vertical swipes (up/down) for hunk navigation
 * 
 * Thresholds and constraints are applied to distinguish intentional gestures from scrolling.
 */
class GestureDetector(
    private val config: GestureConfig = GestureConfig.Default,
    private val density: Density
) {
    /**
     * Detects if the drag gesture qualifies as a swipe based on distance, velocity, and angle
     * 
     * @param dragAmount The total drag distance as an Offset
     * @param velocity The drag velocity in pixels/s
     * @return SwipeDirection if gesture qualifies, null otherwise
     */
    fun detectSwipe(dragAmount: Offset, velocity: Offset): SwipeDirection? {
        val dragX = dragAmount.x
        val dragY = dragAmount.y
        
        // Convert pixel values to dp
        val dragXDp = with(density) { dragX.toDp() }
        val dragYDp = with(density) { dragY.toDp() }
        
        // Convert velocity from pixels/s to dp/s
        val velocityXDp = with(density) { velocity.x.toDp().value }
        val velocityYDp = with(density) { velocity.y.toDp().value }
        
        // Calculate angle in degrees (0 = right, 90 = down, 180 = left, 270 = up)
        val angle = Math.toDegrees(atan2(dragY.toDouble(), dragX.toDouble())).toFloat()
        
        // Determine if gesture is primarily horizontal or vertical
        val absX = abs(dragX)
        val absY = abs(dragY)
        
        // Check horizontal swipe
        if (absX > absY) {
            // Check if angle is close to horizontal (0° or 180°)
            val normalizedAngle = abs(angle)
            if (normalizedAngle <= config.maxAngleDeviation || 
                normalizedAngle >= (180 - config.maxAngleDeviation)) {
                
                // Check if minimum distance is met
                if (abs(dragXDp.value) >= config.horizontalMinDistance.value) {
                    // Check velocity in dp/s
                    if (abs(velocityXDp) >= config.horizontalMinVelocity) {
                        return if (dragX > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
                    }
                }
            }
        } 
        // Check vertical swipe
        else {
            // Check if angle is close to vertical (90° or 270°)
            val verticalAngle = abs(abs(angle) - 90f)
            if (verticalAngle <= config.maxAngleDeviation) {
                
                // Check if minimum distance is met
                if (abs(dragYDp.value) >= config.verticalMinDistance.value) {
                    // Check velocity in dp/s
                    if (abs(velocityYDp) >= config.verticalMinVelocity) {
                        return if (dragY > 0) SwipeDirection.DOWN else SwipeDirection.UP
                    }
                }
            }
        }
        
        return null
    }
}

/**
 * Callbacks for gesture events
 * 
 * Used by GestureDetectionBox to handle swipe gestures in PR review navigation.
 */
data class GestureCallbacks(
    val onSwipeLeft: (() -> Unit)? = null,
    val onSwipeRight: (() -> Unit)? = null,
    val onSwipeUp: (() -> Unit)? = null,
    val onSwipeDown: (() -> Unit)? = null
)
