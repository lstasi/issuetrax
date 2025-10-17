package com.issuetrax.app.presentation.ui.common.gesture

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Overlay that shows visual feedback during swipe gestures
 * Following UI_UX_DESIGN.md specifications for gesture feedback
 */
@Composable
fun SwipeIndicatorOverlay(
    direction: SwipeDirection?,
    progress: Float = 0f,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = direction != null,
        enter = fadeIn(animationSpec = tween(durationMillis = 150)),
        exit = fadeOut(animationSpec = tween(durationMillis = 100)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            direction?.let {
                SwipeDirectionIndicator(
                    direction = it,
                    progress = progress
                )
            }
        }
    }
}

/**
 * Shows the direction indicator with animation based on swipe progress
 */
@Composable
private fun SwipeDirectionIndicator(
    direction: SwipeDirection,
    progress: Float
) {
    // Animate scale based on progress (0.8 to 1.2)
    val scale by animateFloatAsState(
        targetValue = 0.8f + (progress * 0.4f),
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "indicator_scale"
    )
    
    // Animate alpha based on progress (0.6 to 1.0)
    val alpha by animateFloatAsState(
        targetValue = 0.6f + (progress * 0.4f),
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "indicator_alpha"
    )
    
    Column(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = getIconForDirection(direction),
            contentDescription = getDescriptionForDirection(direction),
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = getTextForDirection(direction),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/**
 * Gets the appropriate icon for the swipe direction
 */
private fun getIconForDirection(direction: SwipeDirection): ImageVector {
    return when (direction) {
        SwipeDirection.LEFT -> Icons.Default.ArrowForward // Swipe left = next file (forward)
        SwipeDirection.RIGHT -> Icons.Default.ArrowBack // Swipe right = previous file (back)
        SwipeDirection.UP -> Icons.Default.KeyboardArrowUp
        SwipeDirection.DOWN -> Icons.Default.KeyboardArrowDown
    }
}

/**
 * Gets the description text for accessibility
 */
private fun getDescriptionForDirection(direction: SwipeDirection): String {
    return when (direction) {
        SwipeDirection.LEFT -> "Next file"
        SwipeDirection.RIGHT -> "Previous file"
        SwipeDirection.UP -> "Next hunk"
        SwipeDirection.DOWN -> "Previous hunk"
    }
}

/**
 * Gets the display text for the direction
 */
private fun getTextForDirection(direction: SwipeDirection): String {
    return when (direction) {
        SwipeDirection.LEFT -> "Next File"
        SwipeDirection.RIGHT -> "Previous File"
        SwipeDirection.UP -> "Next Hunk"
        SwipeDirection.DOWN -> "Previous Hunk"
    }
}
