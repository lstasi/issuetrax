# Phase 4: Gesture Navigation - Validation Guide

## Overview

This document provides comprehensive manual testing procedures for Phase 4: Gesture Navigation. Phase 4 enhances the PR review experience with visual and haptic feedback for gesture-based navigation.

**Phase Completion Status**: Implementation Complete  
**Testing Status**: Automated tests passing, manual validation required  
**Last Updated**: 2024

## Features Implemented

### 4.1 Gesture Detection ✅
- [x] GestureDetector for swipe gestures
- [x] Horizontal swipes (left/right) detection
- [x] Vertical swipes (up/down) detection (framework ready, actions reserved for future)
- [x] Velocity thresholds (500dp/s horizontal, 400dp/s vertical)
- [x] Distance thresholds (100dp horizontal, 80dp vertical)
- [x] Angle deviation constraints (30° max)
- [x] Conflict prevention with scroll gestures

### 4.2 Gesture to Action Mapping ✅
- [x] Swipe left → Navigate to next file
- [x] Swipe right → Navigate to previous file
- [x] Swipe up → Reserved for next hunk (future)
- [x] Swipe down → Reserved for previous hunk (future)

### 4.3 Visual Feedback ✅
- [x] SwipeIndicatorOverlay component
- [x] Directional indicators (arrows with text)
- [x] Progress-based animation (scale and alpha)
- [x] Fade-in/fade-out animations (150ms/100ms)
- [x] Semi-transparent dark overlay
- [x] Clear visual cues for each direction

### 4.4 Haptic Feedback ✅
- [x] HapticFeedback utility
- [x] Gesture start feedback (10ms, 50 amplitude)
- [x] Gesture completion feedback (20ms, 100 amplitude)
- [x] Gesture cancellation feedback (5ms, 30 amplitude)
- [x] Android API level compatibility (API 26+)

## Automated Test Coverage

### Unit Tests
- **GestureDetectorTest**: 14 tests ✅
  - Swipe direction detection (left, right, up, down)
  - Velocity threshold validation
  - Distance threshold validation
  - Angle constraint validation
  - Edge cases (zero velocity, zero drag)

- **SwipeIndicatorOverlayTest**: 7 tests ✅
  - Overlay visibility based on direction
  - Indicators for each swipe direction
  - Progress handling (0%, 100%)

- **HapticFeedbackTest**: 3 tests ✅
  - Gesture start vibration
  - Gesture complete vibration
  - Gesture cancel vibration

- **GestureNavigationIntegrationTest**: 5 tests ✅
  - Swipe left/right callback triggering
  - Gesture detection enable/disable
  - Visual feedback rendering
  - Multiple consecutive swipes

**Total Tests**: 29 gesture navigation tests (all passing ✅)

## Manual Testing Procedures

### Prerequisites
- Android device or emulator running API 34 (Android 14)
- Issuetrax app installed
- GitHub Personal Access Token configured
- Repository with pull requests containing multiple files

### Test Environment Setup
1. Launch Issuetrax app
2. Authenticate with GitHub token
3. Select a repository with active pull requests
4. Navigate to a pull request with at least 3 files changed

---

## Test Cases

### Test Case 1: Basic Swipe Left (Next File)
**Priority**: REQUIRED  
**Estimated Time**: 2 minutes

**Steps**:
1. Open a PR with multiple files in the PR review screen
2. Note the current file name and index (e.g., "File 1 of 5")
3. Place finger on the diff view area
4. Swipe left quickly (>500dp/s)
5. Lift finger

**Expected Results**:
- ✅ Visual feedback overlay appears showing "Next File" arrow
- ✅ Subtle vibration felt at gesture start (10ms)
- ✅ Stronger vibration felt at gesture completion (20ms)
- ✅ App navigates to next file (e.g., "File 2 of 5")
- ✅ New file diff is displayed
- ✅ Smooth transition between files

**Pass Criteria**: All expected results achieved

---

### Test Case 2: Basic Swipe Right (Previous File)
**Priority**: REQUIRED  
**Estimated Time**: 2 minutes

**Steps**:
1. Navigate to at least the 2nd file in a PR
2. Note the current file index
3. Place finger on the diff view area
4. Swipe right quickly
5. Lift finger

**Expected Results**:
- ✅ Visual feedback overlay appears showing "Previous File" arrow
- ✅ Subtle vibration at gesture start
- ✅ Stronger vibration at gesture completion
- ✅ App navigates to previous file
- ✅ Previous file diff is displayed

**Pass Criteria**: All expected results achieved

---

### Test Case 3: Swipe with Insufficient Velocity
**Priority**: REQUIRED  
**Estimated Time**: 2 minutes

**Steps**:
1. Open a PR file diff view
2. Place finger on screen
3. Drag slowly to the left (much slower than normal swipe)
4. Lift finger

**Expected Results**:
- ✅ Visual feedback may appear briefly during drag
- ✅ Gesture is NOT recognized (velocity < 500dp/s)
- ✅ Navigation does NOT occur
- ✅ File remains unchanged
- ✅ Gentle cancellation vibration (5ms)

**Pass Criteria**: Gesture properly ignored for slow drags

---

### Test Case 4: Swipe with Insufficient Distance
**Priority**: REQUIRED  
**Estimated Time**: 2 minutes

**Steps**:
1. Open a PR file diff view
2. Place finger on screen
3. Swipe left but only a short distance (~50dp)
4. Lift finger quickly

**Expected Results**:
- ✅ Visual feedback may appear briefly
- ✅ Gesture is NOT recognized (distance < 100dp)
- ✅ Navigation does NOT occur
- ✅ File remains unchanged

**Pass Criteria**: Gesture properly ignored for short swipes

---

### Test Case 5: Diagonal Swipe (Too Much Angle Deviation)
**Priority**: REQUIRED  
**Estimated Time**: 2 minutes

**Steps**:
1. Open a PR file diff view
2. Place finger on screen
3. Swipe diagonally (e.g., left-down at 45° angle)
4. Lift finger

**Expected Results**:
- ✅ Visual feedback may appear briefly
- ✅ Gesture is NOT recognized (angle > 30°)
- ✅ Navigation does NOT occur
- ✅ File remains unchanged

**Pass Criteria**: Gesture properly ignored for diagonal swipes

---

### Test Case 6: Swipe at File Boundaries
**Priority**: REQUIRED  
**Estimated Time**: 3 minutes

**Scenario A - At Last File**:
1. Navigate to the last file in a PR
2. Attempt to swipe left (next file)

**Expected Results**:
- ✅ Gesture is detected
- ✅ Visual feedback appears
- ✅ Vibration occurs
- ✅ Navigation does NOT occur (already at last file)
- ✅ User remains on last file

**Scenario B - At First File**:
1. Navigate to the first file in a PR
2. Attempt to swipe right (previous file)

**Expected Results**:
- ✅ Gesture is detected
- ✅ Visual feedback appears
- ✅ Vibration occurs
- ✅ Navigation does NOT occur (already at first file)
- ✅ User remains on first file

**Pass Criteria**: Boundaries respected, no crashes

---

### Test Case 7: Multiple Consecutive Swipes
**Priority**: REQUIRED  
**Estimated Time**: 3 minutes

**Steps**:
1. Open a PR with at least 5 files
2. Start at file 1
3. Swipe left 3 times quickly
4. Verify navigation to files 2, 3, 4
5. Swipe right 2 times
6. Verify navigation back to files 3, 2

**Expected Results**:
- ✅ Each swipe is detected independently
- ✅ Visual feedback shows for each swipe
- ✅ Haptic feedback occurs for each swipe
- ✅ Navigation occurs correctly for each gesture
- ✅ No gestures are missed or doubled

**Pass Criteria**: All swipes detected and executed correctly

---

### Test Case 8: Gesture During Scroll
**Priority**: REQUIRED  
**Estimated Time**: 3 minutes

**Steps**:
1. Open a PR with a large diff (requires scrolling)
2. Scroll vertically through the diff
3. While scrolling, attempt to swipe left/right
4. Complete the swipe gesture

**Expected Results**:
- ✅ Vertical scrolling works normally
- ✅ Horizontal swipe is detected if gesture is sufficiently horizontal
- ✅ Scrolling and swiping do not interfere with each other
- ✅ User can distinguish between scroll and swipe

**Pass Criteria**: Scroll and swipe gestures coexist without conflicts

---

### Test Case 9: Visual Feedback Animation
**Priority**: REQUIRED  
**Estimated Time**: 3 minutes

**Steps**:
1. Open a PR file diff view
2. Perform a slow, deliberate swipe left
3. Observe the visual feedback during the swipe
4. Complete the gesture

**Expected Results**:
- ✅ Dark semi-transparent overlay appears
- ✅ Arrow icon (→) is centered and visible
- ✅ "Next File" text appears below arrow
- ✅ Icon and text scale/fade based on progress
- ✅ Overlay fades out smoothly after gesture completion (100ms)

**Pass Criteria**: Visual feedback is smooth and informative

---

### Test Case 10: Haptic Feedback Intensity
**Priority**: REQUIRED  
**Estimated Time**: 3 minutes

**Prerequisites**: Physical device (emulator cannot test haptics)

**Steps**:
1. Hold device in hand to feel vibrations clearly
2. Perform a swipe left gesture
3. Note the vibration pattern
4. Perform a swipe right gesture
5. Note the vibration pattern
6. Perform a cancelled gesture (start swipe, then cancel)

**Expected Results**:
- ✅ Start vibration (10ms) is subtle but noticeable
- ✅ Completion vibration (20ms) is noticeably stronger
- ✅ Cancellation vibration (5ms) is very gentle
- ✅ Vibrations feel responsive and appropriate
- ✅ Vibration timing aligns with visual feedback

**Pass Criteria**: Haptic feedback is appropriate and well-timed

---

### Test Case 11: Gesture in Inline View
**Priority**: REQUIRED  
**Estimated Time**: 2 minutes

**Steps**:
1. Open a PR file diff view
2. Ensure "Inline View" is selected
3. Perform swipe left gesture
4. Perform swipe right gesture

**Expected Results**:
- ✅ Gestures work correctly in inline view
- ✅ Visual feedback appears
- ✅ Haptic feedback occurs
- ✅ Navigation works properly

**Pass Criteria**: Gestures work in inline view mode

---

### Test Case 12: Gesture in Standard View
**Priority**: REQUIRED  
**Estimated Time**: 2 minutes

**Steps**:
1. Open a PR file diff view
2. Switch to "Standard View"
3. Perform swipe left gesture
4. Perform swipe right gesture

**Expected Results**:
- ✅ Gestures work correctly in standard view
- ✅ Visual feedback appears
- ✅ Haptic feedback occurs
- ✅ Navigation works properly

**Pass Criteria**: Gestures work in standard view mode

---

### Test Case 13: Gesture with Different Screen Sizes
**Priority**: OPTIONAL  
**Estimated Time**: 10 minutes

**Steps**:
1. Test on a small phone (5" screen)
2. Test on a medium phone (6" screen)
3. Test on a large phone (6.5"+ screen)
4. Test on a tablet (if available)

**Expected Results**:
- ✅ Gesture thresholds work appropriately on all sizes
- ✅ Visual feedback scales appropriately
- ✅ Swipe detection is consistent across sizes

**Pass Criteria**: Gestures work consistently on different screen sizes

---

### Test Case 14: Gesture Sensitivity Comparison with GitHub Web
**Priority**: OPTIONAL  
**Estimated Time**: 5 minutes

**Steps**:
1. Open the same PR in GitHub web interface on desktop
2. Compare the file navigation experience
3. Navigate through files in Issuetrax using gestures
4. Compare ease of use and efficiency

**Expected Results**:
- ✅ Gesture navigation is faster than clicking arrows
- ✅ Gesture navigation feels natural on mobile
- ✅ Visual feedback helps user understand available actions

**Pass Criteria**: Gesture navigation provides better mobile UX than traditional buttons

---

### Test Case 15: Accessibility - Visual Feedback Without Haptic
**Priority**: REQUIRED  
**Estimated Time**: 3 minutes

**Prerequisites**: Disable device vibration in system settings

**Steps**:
1. Turn off device vibration
2. Open a PR file diff view
3. Perform swipe gestures

**Expected Results**:
- ✅ Gestures still work correctly
- ✅ Visual feedback provides sufficient cues
- ✅ User can still understand gesture actions without haptic

**Pass Criteria**: Visual feedback alone is sufficient

---

## Performance Benchmarks

### Gesture Response Time
- **Target**: < 50ms from gesture start to visual feedback
- **Measurement**: Use device developer tools or manual observation
- **Pass Criteria**: Visual feedback appears immediately on swipe start

### Navigation Speed
- **Target**: < 300ms from gesture completion to new file display
- **Measurement**: Manual observation with stopwatch
- **Pass Criteria**: File transition feels smooth and immediate

### Memory Impact
- **Target**: No significant memory increase from gesture detection
- **Measurement**: Android Studio Profiler
- **Pass Criteria**: Memory usage remains stable during repeated gestures

---

## Known Limitations

1. **Vertical Swipes (Up/Down)**: Framework is implemented but actions are reserved for future hunk navigation
2. **Custom Gesture Sensitivity**: Configuration is fixed, not user-adjustable
3. **Haptic Feedback on Emulator**: Cannot be tested, requires physical device
4. **Visual Feedback Animation**: Performance depends on device capabilities

---

## Issue Reporting

If you encounter issues during validation:

1. **Note the Test Case Number** (e.g., Test Case 3)
2. **Document the Issue**:
   - Device model and Android version
   - App version
   - Steps to reproduce
   - Expected vs actual behavior
   - Screenshots or screen recording if applicable
3. **Severity Classification**:
   - **Critical**: Crashes, data loss, complete feature failure
   - **High**: Gestures not detected, wrong navigation, no feedback
   - **Medium**: Inconsistent behavior, poor feedback timing
   - **Low**: Minor visual issues, slight timing differences

---

## Validation Checklist

Use this checklist to track validation progress:

### Core Functionality
- [ ] Test Case 1: Basic Swipe Left
- [ ] Test Case 2: Basic Swipe Right
- [ ] Test Case 3: Insufficient Velocity
- [ ] Test Case 4: Insufficient Distance
- [ ] Test Case 5: Diagonal Swipe
- [ ] Test Case 6: Swipe at Boundaries
- [ ] Test Case 7: Multiple Consecutive Swipes
- [ ] Test Case 8: Gesture During Scroll

### Visual Feedback
- [ ] Test Case 9: Visual Feedback Animation
- [ ] Test Case 11: Gesture in Inline View
- [ ] Test Case 12: Gesture in Standard View

### Haptic Feedback (Physical Device Required)
- [ ] Test Case 10: Haptic Feedback Intensity

### Optional/Extended Tests
- [ ] Test Case 13: Different Screen Sizes
- [ ] Test Case 14: Comparison with GitHub Web
- [ ] Test Case 15: Accessibility without Haptic

---

## Validation Sign-Off

**Validated By**: ___________________  
**Date**: ___________________  
**Device(s) Tested**: ___________________  
**Overall Result**: [ ] PASS [ ] PASS WITH ISSUES [ ] FAIL  

**Notes**:
_______________________________________________
_______________________________________________
_______________________________________________

---

**Document Version**: 1.0  
**Phase**: 4 - Gesture Navigation  
**Status**: Ready for Validation
