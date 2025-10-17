package com.issuetrax.app.presentation.ui.common.gesture

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Utility for providing haptic feedback during gesture interactions
 * Following UI_UX_DESIGN.md specifications for gesture feedback
 */
object HapticFeedback {
    
    /**
     * Provides subtle haptic feedback for gesture start (10ms)
     */
    fun gestureStart(context: Context) {
        vibrate(context, durationMs = 10, amplitude = 50)
    }
    
    /**
     * Provides stronger haptic feedback for successful gesture completion (20ms)
     */
    fun gestureComplete(context: Context) {
        vibrate(context, durationMs = 20, amplitude = 100)
    }
    
    /**
     * Provides gentle haptic feedback for gesture cancellation (5ms)
     */
    fun gestureCancel(context: Context) {
        vibrate(context, durationMs = 5, amplitude = 30)
    }
    
    /**
     * Triggers vibration with the specified duration and amplitude
     */
    private fun vibrate(context: Context, durationMs: Long, amplitude: Int) {
        val vibrator = getVibrator(context) ?: return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use VibrationEffect for API 26+ (Android 8.0+)
            val effect = VibrationEffect.createOneShot(
                durationMs,
                amplitude
            )
            vibrator.vibrate(effect)
        } else {
            // Fallback for older versions (deprecated but necessary for compatibility)
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
    
    /**
     * Gets the Vibrator service from the system
     */
    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31+ (Android 12+) uses VibratorManager
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            // API 30 and below use Vibrator directly
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
}
