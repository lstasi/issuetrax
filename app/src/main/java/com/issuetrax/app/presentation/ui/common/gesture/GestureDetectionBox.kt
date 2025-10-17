package com.issuetrax.app.presentation.ui.common.gesture

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.detectDragGestures
import kotlin.math.abs

/**
 * Enhanced gesture detection with visual and haptic feedback
 * 
 * Wraps content with swipe gesture detection and provides:
 * - Visual feedback overlay showing swipe direction
 * - Haptic feedback on gesture start and completion
 * - Progress-based animation
 * 
 * @param callbacks Gesture event callbacks
 * @param config Gesture detection configuration
 * @param enabled Whether gesture detection is enabled
 * @param showVisualFeedback Whether to show visual feedback overlay
 * @param enableHapticFeedback Whether to enable haptic feedback
 * @param content The content to wrap with gesture detection
 */
@Composable
fun GestureDetectionBox(
    callbacks: GestureCallbacks,
    config: GestureConfig = GestureConfig.Default,
    enabled: Boolean = true,
    showVisualFeedback: Boolean = true,
    enableHapticFeedback: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val gestureDetector = remember(config, density) { 
        GestureDetector(config, density) 
    }
    
    var currentDirection by remember { mutableStateOf<SwipeDirection?>(null) }
    var gestureProgress by remember { mutableFloatStateOf(0f) }
    var hasProvidedStartFeedback by remember { mutableStateOf(false) }
    
    Box(modifier = modifier) {
        // Content
        Box(
            modifier = if (enabled) {
                Modifier.pointerInput(Unit) {
                    var dragStart = Offset.Zero
                    var dragEnd = Offset.Zero
                    var dragStartTime = 0L
                    
                    detectDragGestures(
                        onDragStart = { offset ->
                            dragStart = offset
                            dragEnd = offset
                            dragStartTime = System.currentTimeMillis()
                            hasProvidedStartFeedback = false
                            currentDirection = null
                            gestureProgress = 0f
                        },
                        onDragEnd = {
                            val dragAmount = dragEnd - dragStart
                            val dragDuration = (System.currentTimeMillis() - dragStartTime).toFloat() / 1000f
                            
                            // Calculate velocity in pixels per second
                            val velocity = if (dragDuration > 0) {
                                Offset(
                                    dragAmount.x / dragDuration,
                                    dragAmount.y / dragDuration
                                )
                            } else {
                                Offset.Zero
                            }
                            
                            // Detect swipe direction
                            val detectedDirection = gestureDetector.detectSwipe(dragAmount, velocity)
                            
                            if (detectedDirection != null) {
                                // Provide haptic feedback for successful gesture
                                if (enableHapticFeedback) {
                                    HapticFeedback.gestureComplete(context)
                                }
                                
                                // Execute callback
                                when (detectedDirection) {
                                    SwipeDirection.LEFT -> callbacks.onSwipeLeft?.invoke()
                                    SwipeDirection.RIGHT -> callbacks.onSwipeRight?.invoke()
                                    SwipeDirection.UP -> callbacks.onSwipeUp?.invoke()
                                    SwipeDirection.DOWN -> callbacks.onSwipeDown?.invoke()
                                }
                            } else {
                                // Provide haptic feedback for cancelled gesture
                                if (enableHapticFeedback && hasProvidedStartFeedback) {
                                    HapticFeedback.gestureCancel(context)
                                }
                            }
                            
                            // Reset state
                            currentDirection = null
                            gestureProgress = 0f
                            dragStart = Offset.Zero
                            dragEnd = Offset.Zero
                            hasProvidedStartFeedback = false
                        },
                        onDragCancel = {
                            // Provide haptic feedback for cancelled gesture
                            if (enableHapticFeedback && hasProvidedStartFeedback) {
                                HapticFeedback.gestureCancel(context)
                            }
                            
                            // Reset state on cancel
                            currentDirection = null
                            gestureProgress = 0f
                            dragStart = Offset.Zero
                            dragEnd = Offset.Zero
                            hasProvidedStartFeedback = false
                        },
                        onDrag = { change, dragAmount ->
                            dragEnd += dragAmount
                            change.consume()
                            
                            val totalDrag = dragEnd - dragStart
                            
                            // Determine tentative direction based on current drag
                            val tentativeDirection = determineTentativeDirection(totalDrag, density, config)
                            
                            if (tentativeDirection != null && tentativeDirection != currentDirection) {
                                currentDirection = tentativeDirection
                                
                                // Provide haptic feedback on gesture start (first time direction is detected)
                                if (!hasProvidedStartFeedback && enableHapticFeedback) {
                                    HapticFeedback.gestureStart(context)
                                    hasProvidedStartFeedback = true
                                }
                            }
                            
                            // Update progress (0.0 to 1.0 based on drag distance)
                            if (tentativeDirection != null) {
                                gestureProgress = calculateProgress(totalDrag, tentativeDirection, density, config)
                            }
                        }
                    )
                }
            } else {
                Modifier
            }
        ) {
            content()
        }
        
        // Visual feedback overlay
        if (showVisualFeedback) {
            SwipeIndicatorOverlay(
                direction = currentDirection,
                progress = gestureProgress
            )
        }
    }
}

/**
 * Determines the tentative direction of the swipe based on current drag amount
 */
private fun determineTentativeDirection(
    dragAmount: Offset,
    density: androidx.compose.ui.unit.Density,
    config: GestureConfig
): SwipeDirection? {
    val dragX = dragAmount.x
    val dragY = dragAmount.y
    
    val absX = abs(dragX)
    val absY = abs(dragY)
    
    // Check if primarily horizontal
    if (absX > absY) {
        // Check minimum distance threshold (50% of required distance)
        val dragXDp = with(density) { absX.toDp() }
        if (dragXDp.value >= config.horizontalMinDistance.value * 0.5f) {
            return if (dragX > 0) SwipeDirection.RIGHT else SwipeDirection.LEFT
        }
    } 
    // Check if primarily vertical
    else {
        // Check minimum distance threshold (50% of required distance)
        val dragYDp = with(density) { absY.toDp() }
        if (dragYDp.value >= config.verticalMinDistance.value * 0.5f) {
            return if (dragY > 0) SwipeDirection.DOWN else SwipeDirection.UP
        }
    }
    
    return null
}

/**
 * Calculates the progress of the gesture (0.0 to 1.0)
 */
private fun calculateProgress(
    dragAmount: Offset,
    direction: SwipeDirection,
    density: androidx.compose.ui.unit.Density,
    config: GestureConfig
): Float {
    return when (direction) {
        SwipeDirection.LEFT, SwipeDirection.RIGHT -> {
            val dragXDp = with(density) { abs(dragAmount.x).toDp() }
            val progress = dragXDp.value / config.horizontalMinDistance.value
            progress.coerceIn(0f, 1f)
        }
        SwipeDirection.UP, SwipeDirection.DOWN -> {
            val dragYDp = with(density) { abs(dragAmount.y).toDp() }
            val progress = dragYDp.value / config.verticalMinDistance.value
            progress.coerceIn(0f, 1f)
        }
    }
}
