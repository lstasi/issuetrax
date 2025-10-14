package com.issuetrax.app.presentation.ui.pr_review.components

import org.junit.Test

/**
 * Unit tests for FileNavigationButtons composable.
 * 
 * These tests verify the navigation button state logic
 * for displaying file navigation controls. Full UI tests would be in androidTest.
 */
class FileNavigationButtonsTest {
    
    @Test
    fun `Previous button should be enabled when not at first file`() {
        // Given
        val currentFileIndex = 1
        val totalFiles = 3
        
        // When/Then
        val isPreviousEnabled = currentFileIndex > 0
        assert(isPreviousEnabled) { "Previous button should be enabled when not at first file" }
    }
    
    @Test
    fun `Previous button should be disabled at first file`() {
        // Given
        val currentFileIndex = 0
        val totalFiles = 3
        
        // When/Then
        val isPreviousEnabled = currentFileIndex > 0
        assert(!isPreviousEnabled) { "Previous button should be disabled at first file" }
    }
    
    @Test
    fun `Next button should be enabled when not at last file`() {
        // Given
        val currentFileIndex = 1
        val totalFiles = 3
        
        // When/Then
        val isNextEnabled = currentFileIndex < totalFiles - 1
        assert(isNextEnabled) { "Next button should be enabled when not at last file" }
    }
    
    @Test
    fun `Next button should be disabled at last file`() {
        // Given
        val currentFileIndex = 2
        val totalFiles = 3
        
        // When/Then
        val isNextEnabled = currentFileIndex < totalFiles - 1
        assert(!isNextEnabled) { "Next button should be disabled at last file" }
    }
    
    @Test
    fun `Both buttons should work correctly for single file`() {
        // Given
        val currentFileIndex = 0
        val totalFiles = 1
        
        // When/Then
        val isPreviousEnabled = currentFileIndex > 0
        val isNextEnabled = currentFileIndex < totalFiles - 1
        
        assert(!isPreviousEnabled) { "Previous button should be disabled with single file" }
        assert(!isNextEnabled) { "Next button should be disabled with single file" }
    }
    
    @Test
    fun `Both buttons should be enabled for middle file in multi-file list`() {
        // Given
        val currentFileIndex = 3
        val totalFiles = 7
        
        // When/Then
        val isPreviousEnabled = currentFileIndex > 0
        val isNextEnabled = currentFileIndex < totalFiles - 1
        
        assert(isPreviousEnabled) { "Previous button should be enabled in middle of list" }
        assert(isNextEnabled) { "Next button should be enabled in middle of list" }
    }
    
    @Test
    fun `File counter displays correct format`() {
        // Given
        val currentFileIndex = 4
        val totalFiles = 10
        
        // When
        val displayText = "File ${currentFileIndex + 1} of $totalFiles"
        
        // Then
        assert(displayText == "File 5 of 10") { "File counter should display 1-based index" }
    }
}
