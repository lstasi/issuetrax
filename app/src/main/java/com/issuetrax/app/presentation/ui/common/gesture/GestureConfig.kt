package com.issuetrax.app.presentation.ui.common.gesture

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration for gesture detection based on UI_UX_DESIGN.md specifications
 */
data class GestureConfig(
    // Horizontal swipe thresholds (for file navigation)
    val horizontalMinDistance: Dp = 100.dp,
    val horizontalMinVelocity: Float = 500f, // dp/s
    
    // Vertical swipe thresholds (for hunk navigation)
    val verticalMinDistance: Dp = 80.dp,
    val verticalMinVelocity: Float = 400f, // dp/s
    
    // Maximum angle deviation from horizontal/vertical axis
    val maxAngleDeviation: Float = 30f, // degrees
    
    // Movement tolerance for distinguishing from scrolling
    val scrollTolerance: Dp = 10.dp
) {
    companion object {
        /**
         * Default gesture configuration following the design specifications
         */
        val Default = GestureConfig()
    }
}
