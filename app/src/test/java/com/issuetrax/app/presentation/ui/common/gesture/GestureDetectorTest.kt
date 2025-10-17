package com.issuetrax.app.presentation.ui.common.gesture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for GestureDetector
 * 
 * Tests gesture detection logic including:
 * - Swipe direction detection
 * - Velocity threshold validation
 * - Distance threshold validation
 * - Angle constraint validation
 */
class GestureDetectorTest {
    
    private lateinit var gestureDetector: GestureDetector
    private lateinit var density: Density
    private lateinit var config: GestureConfig
    
    @Before
    fun setup() {
        // Mock density (typical phone screen density)
        density = object : Density {
            override val density: Float = 2.5f
            override val fontScale: Float = 1.0f
        }
        config = GestureConfig.Default
        gestureDetector = GestureDetector(config, density)
    }
    
    @Test
    fun `swipe right with sufficient distance and velocity is detected`() {
        // Arrange
        // 100dp = 250px at 2.5 density, 500dp/s = 1250px/s
        val dragAmount = Offset(x = 300f, y = 10f) // 120dp horizontal, 4dp vertical
        val velocity = Offset(x = 1500f, y = 100f) // 600dp/s horizontal
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertEquals(SwipeDirection.RIGHT, result)
    }
    
    @Test
    fun `swipe left with sufficient distance and velocity is detected`() {
        // Arrange
        val dragAmount = Offset(x = -300f, y = -10f) // 120dp horizontal, 4dp vertical
        val velocity = Offset(x = -1500f, y = -100f) // 600dp/s horizontal
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertEquals(SwipeDirection.LEFT, result)
    }
    
    @Test
    fun `swipe down with sufficient distance and velocity is detected`() {
        // Arrange
        // 80dp = 200px at 2.5 density, 400dp/s = 1000px/s
        val dragAmount = Offset(x = 10f, y = 250f) // 4dp horizontal, 100dp vertical
        val velocity = Offset(x = 100f, y = 1200f) // 480dp/s vertical
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertEquals(SwipeDirection.DOWN, result)
    }
    
    @Test
    fun `swipe up with sufficient distance and velocity is detected`() {
        // Arrange
        val dragAmount = Offset(x = -10f, y = -250f) // 4dp horizontal, 100dp vertical
        val velocity = Offset(x = -100f, y = -1200f) // 480dp/s vertical
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertEquals(SwipeDirection.UP, result)
    }
    
    @Test
    fun `swipe with insufficient horizontal distance is not detected`() {
        // Arrange
        // Below minimum horizontal distance of 100dp
        val dragAmount = Offset(x = 200f, y = 10f) // 80dp horizontal
        val velocity = Offset(x = 1500f, y = 100f) // Sufficient velocity
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
    
    @Test
    fun `swipe with insufficient horizontal velocity is not detected`() {
        // Arrange
        // Below minimum horizontal velocity of 500dp/s
        val dragAmount = Offset(x = 300f, y = 10f) // Sufficient distance
        val velocity = Offset(x = 1000f, y = 100f) // 400dp/s - insufficient
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
    
    @Test
    fun `swipe with insufficient vertical distance is not detected`() {
        // Arrange
        // Below minimum vertical distance of 80dp
        val dragAmount = Offset(x = 10f, y = 150f) // 60dp vertical
        val velocity = Offset(x = 100f, y = 1200f) // Sufficient velocity
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
    
    @Test
    fun `swipe with insufficient vertical velocity is not detected`() {
        // Arrange
        // Below minimum vertical velocity of 400dp/s
        val dragAmount = Offset(x = 10f, y = 250f) // Sufficient distance
        val velocity = Offset(x = 100f, y = 800f) // 320dp/s - insufficient
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
    
    @Test
    fun `horizontal swipe with too much vertical deviation is not detected`() {
        // Arrange
        // Angle exceeds max deviation of 30 degrees
        val dragAmount = Offset(x = 300f, y = 200f) // ~33.7 degrees from horizontal
        val velocity = Offset(x = 1500f, y = 1000f) // Sufficient velocity
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
    
    @Test
    fun `vertical swipe with too much horizontal deviation is not detected`() {
        // Arrange
        // Angle exceeds max deviation of 30 degrees from vertical
        val dragAmount = Offset(x = 200f, y = 250f) // ~38.7 degrees from vertical
        val velocity = Offset(x = 1000f, y = 1200f) // Sufficient velocity
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
    
    @Test
    fun `horizontal swipe at exactly max angle deviation is detected`() {
        // Arrange
        // At exactly 30 degrees from horizontal
        val distance = 300f // pixels
        val angle = Math.toRadians(30.0)
        val dragAmount = Offset(
            x = (distance * Math.cos(angle)).toFloat(),
            y = (distance * Math.sin(angle)).toFloat()
        )
        val velocity = Offset(x = 1500f, y = 866f) // Sufficient velocity, same angle
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertEquals(SwipeDirection.RIGHT, result)
    }
    
    @Test
    fun `vertical swipe at exactly max angle deviation is detected`() {
        // Arrange
        // At exactly 30 degrees from vertical (60 degrees from horizontal)
        val distance = 250f // pixels
        val angle = Math.toRadians(60.0) // 30 degrees from vertical
        val dragAmount = Offset(
            x = (distance * Math.cos(angle)).toFloat(),
            y = (distance * Math.sin(angle)).toFloat()
        )
        val velocity = Offset(x = 600f, y = 1200f) // Sufficient velocity
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertEquals(SwipeDirection.DOWN, result)
    }
    
    @Test
    fun `zero velocity gesture is not detected`() {
        // Arrange
        val dragAmount = Offset(x = 300f, y = 10f)
        val velocity = Offset.Zero
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
    
    @Test
    fun `zero drag amount is not detected`() {
        // Arrange
        val dragAmount = Offset.Zero
        val velocity = Offset(x = 1500f, y = 100f)
        
        // Act
        val result = gestureDetector.detectSwipe(dragAmount, velocity)
        
        // Assert
        assertNull(result)
    }
}
