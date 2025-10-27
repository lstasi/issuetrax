# PR Review Feature - Validation Guide

This document provides manual validation procedures for the PR Review Screen feature.

## Overview

The PR Review feature has been refactored to provide a simplified navigation experience:
- View pull request details in the top bar
- Browse through changed files in a PR (main view)
- Tap a file to view its diff
- Swipe right to return from diff to file list
- Tap a chunk to view it in full screen
- Close button to return from chunk to diff

## Automated Test Coverage

Comprehensive automated tests are located in:
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewViewModelTest.kt` (Unit tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewIntegrationTest.kt` (Integration tests)

### Test Coverage Summary

✅ **Scenario 1: File List View**
- Loading PR and displaying file list
- File list shows all changed files
- File metadata (additions, deletions, status) displayed correctly

✅ **Scenario 2: File Navigation**
- Tap to navigate to file diff
- Swipe right to return to file list
- View state transitions work correctly

✅ **Scenario 3: Chunk Detail View**
- Navigate to chunk detail view
- Close button returns to file diff
- Selected chunk is displayed correctly

✅ **Scenario 4: Error Handling**
- PR load failure
- File list load failure (after successful PR load)
- Empty PR (0 files)
- Error recovery

✅ **Scenario 5: Data Integrity**
- PR metadata verification
- File diff data completeness
- Addition/deletion counts
- File status types (ADDED, MODIFIED, REMOVED, RENAMED)

## Manual Validation Procedures

### Prerequisites

1. Android device or emulator with API 34+ (Android 14)
2. GitHub Personal Access Token configured in the app
3. Access to a repository with various types of PRs

### Test Case 1: File List Display

**Objective**: Verify the app displays the file list as the main view.

**Steps**:
1. Navigate to Current Work screen
2. Select a repository with a PR
3. Tap on the PR to open PR Review screen

**Expected Results**:
- PR metadata displays in top bar (title, repo/PR number)
- **File list is the main view** (default state)
- All changed files are listed
- Each file shows:
  - File name
  - Status icon (added/modified/removed)
  - Addition/deletion counts
  - Status label

**Pass Criteria**:
- [ ] File list displays by default
- [ ] All files are shown
- [ ] File metadata is correct
- [ ] No PR metadata card on top (metadata in top bar only)

### Test Case 2: Navigate to File Diff

**Objective**: Verify tapping a file navigates to the diff view.

**Steps**:
1. Open a PR with multiple files (from Test Case 1)
2. Tap on any file in the list

**Expected Results**:
- View changes to show file diff
- Diff displays with file name and stats
- All chunks/hunks are visible
- **No Previous/Next buttons** (removed)
- Can scroll through the diff

**Pass Criteria**:
- [ ] Tapping file shows diff
- [ ] Diff is readable
- [ ] No navigation buttons present

### Test Case 3: Return to File List with Swipe

**Objective**: Verify swiping right returns to the file list.

**Steps**:
1. From file diff view (Test Case 2)
2. Swipe from left edge to the right (swipe right gesture)

**Expected Results**:
- View changes back to file list
- No file is selected/highlighted
- Can tap another file

**Pass Criteria**:
- [ ] Swipe right returns to file list
- [ ] Haptic feedback on swipe
- [ ] Visual feedback during swipe

### Test Case 4: View Chunk in Full Screen

**Objective**: Verify tapping a chunk shows it in full screen.

**Steps**:
1. Navigate to a file diff (Test Case 2)
2. Tap on any chunk/hunk in the diff

**Expected Results**:
- View changes to full-screen chunk detail
- Chunk header shows "Chunk X of Y"
- File name displayed in subtitle
- **Close button (✕) in top right corner**
- Chunk content is readable

**Pass Criteria**:
- [ ] Chunk displays in full screen
- [ ] Close button is visible
- [ ] Chunk content is correct

### Test Case 5: Close Chunk Detail

**Objective**: Verify close button returns to file diff.

**Steps**:
1. From chunk detail view (Test Case 4)
2. Tap the close button (✕) in top right

**Expected Results**:
- View returns to file diff
- Same file is still displayed
- Can scroll to other chunks

**Pass Criteria**:
- [ ] Close button works
- [ ] Returns to correct diff view

### Test Case 6: Error Handling - Network Error

**Objective**: Verify the app handles network errors gracefully.

**Steps**:
1. Navigate to Current Work screen
2. Enable airplane mode or disconnect network
3. Try to open a PR that hasn't been cached

**Expected Results**:
- Loading indicator appears briefly
- Error message displays clearly
- Error text is **selectable** (wrapped in SelectionContainer)
- Retry button or back navigation available
- App does not crash
- Error message is helpful (e.g., "Network error", "Unable to fetch PR")

**Pass Criteria**:
- [ ] Error message displays
- [ ] Error text can be selected and copied
- [ ] App remains stable
- [ ] User can navigate back

### Test Case 7: Data Verification

**Objective**: Verify displayed data matches GitHub web interface.

**Steps**:
1. Open a specific PR in the app (note the PR number)
2. Open the same PR in GitHub web interface
3. Compare the data points

**Data to Verify**:
- PR number
- PR title
- PR state (Open/Closed/Merged)
- Number of changed files
- Total additions count
- Total deletions count
- List of changed files (filenames)
- Each file's status (added/modified/removed)
- Each file's addition/deletion counts

**Expected Results**:
- All data points match exactly between app and web
- File order may differ (this is acceptable)
- Counts are accurate

**Pass Criteria**:
- [ ] PR metadata matches
- [ ] File count matches
- [ ] Addition/deletion totals match
- [ ] Individual file stats match
- [ ] File names are correct

### Test Case 8: Empty PR

**Objective**: Verify handling of PRs with no files.

**Steps**:
1. Open a PR with no changed files (if available)

**Expected Results**:
- File list view is shown
- Message displays: "No files changed in this pull request"
- No errors or crashes

**Pass Criteria**:
- [ ] Empty state handled gracefully
- [ ] Clear message shown

## Known Limitations and Changes

1. **No Previous/Next Buttons**: Removed for simplification. Users navigate by tapping files in the list.
2. **No Swipe Left/Right for Files**: File navigation via swipe gestures has been removed.
3. **Single Swipe Gesture**: Only swipe right (from diff to list) is supported.
4. **No PR Metadata Card**: PR metadata is shown in the top bar instead of a dedicated card.

## UI/UX Verification

**Objective**: Verify the overall user experience is smooth and intuitive.

**Steps**:
1. Open several different PRs
2. Navigate through the flow: file list → diff → chunk detail
3. Test swipe gestures
4. Observe animations, transitions, and responsiveness

**Expected Results**:
- UI is responsive (no lag)
- Touch targets are adequate (48dp minimum)
- Text is readable
- Layout adapts to different screen sizes
- Scrolling is smooth
- Gestures work reliably

**Pass Criteria**:
- [ ] No performance issues
- [ ] Clear visual feedback
- [ ] Intuitive navigation
- [ ] Professional appearance

## Test Execution Checklist

### Automated Tests
- [x] PRReviewViewModelTest - All tests passing
- [x] PRReviewIntegrationTest - Tests updated for new navigation

### Manual Tests
- [ ] Test Case 1: File List Display
- [ ] Test Case 2: Navigate to File Diff
- [ ] Test Case 3: Return to File List with Swipe
- [ ] Test Case 4: View Chunk in Full Screen
- [ ] Test Case 5: Close Chunk Detail
- [ ] Test Case 6: Error Handling - Network Error
- [ ] Test Case 7: Data Verification
- [ ] Test Case 8: Empty PR
- [ ] UI/UX Verification

## References

- **Architecture**: `doc/ARCHITECTURE.md`
- **UI/UX Design**: `doc/UI_UX_DESIGN.md`
- **Source Code**: 
  - `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/`
  - `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/`

---

**Document Version**: 2.0  
**Last Updated**: PR View Refactor  
**Status**: Ready for validation
