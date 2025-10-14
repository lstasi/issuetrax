# PR Review Feature - Validation Guide

This document provides manual validation procedures for the PR Review Screen feature (Phase 2, Task 2.5).

## Overview

The PR Review feature allows users to:
- View pull request details and metadata
- Browse through changed files in a PR
- Navigate between files using Previous/Next buttons
- Jump directly to specific files by clicking on them in the file list
- Handle various edge cases (empty PRs, single file PRs, large PRs)

## Automated Test Coverage

Comprehensive automated tests are located in:
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewViewModelTest.kt` (Unit tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/PRReviewIntegrationTest.kt` (Integration tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/components/FileNavigationButtonsTest.kt` (Component tests)

### Test Coverage Summary

✅ **Scenario 1: Single File PR**
- Loading PR with exactly 1 file
- Verifying navigation buttons are disabled
- Attempting navigation with single file (should not change state)

✅ **Scenario 2: Multi-File PR**
- Loading PR with many files (tested with 5, 8, and 10 files)
- Sequential navigation through all files
- Verifying file list completeness
- Boundary testing (first/last file)

✅ **Scenario 3: File Navigation**
- Next/Previous button navigation
- Jump navigation (navigateToFile)
- Invalid index handling (negative, out of bounds)
- Navigation state consistency

✅ **Scenario 4: Error Handling**
- PR load failure
- File list load failure (after successful PR load)
- Empty PR (0 files)
- Error recovery (clearError)

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

### Test Case 1: PR with Single File

**Objective**: Verify the app correctly handles PRs with exactly one changed file.

**Steps**:
1. Navigate to Current Work screen
2. Select a repository with a PR that has 1 changed file
3. Tap on the PR to open PR Review screen

**Expected Results**:
- PR metadata displays correctly (title, author, description)
- File list shows exactly 1 file
- File counter shows "File 1 of 1"
- **Previous button is disabled** (grayed out)
- **Next button is disabled** (grayed out)
- Current file is highlighted in the file list
- File shows correct status (added/modified/removed)
- Addition/deletion counts are displayed

**Pass Criteria**:
- [ ] All metadata displays correctly
- [ ] Navigation buttons are disabled
- [ ] No crashes when tapping disabled buttons
- [ ] File information is accurate

### Test Case 2: PR with Many Files (5+ files)

**Objective**: Verify the app correctly handles PRs with multiple changed files.

**Steps**:
1. Navigate to Current Work screen
2. Select a repository with a PR that has 5 or more changed files
3. Tap on the PR to open PR Review screen

**Expected Results**:
- PR metadata displays correctly
- File list shows all changed files
- File counter shows "File 1 of X" (where X is total files)
- **Previous button is disabled** at first file
- **Next button is enabled** at first file
- Current file (first one) is highlighted in the file list

**Pass Criteria**:
- [ ] All files appear in the list
- [ ] File count matches GitHub web interface
- [ ] Navigation state is correct
- [ ] No performance issues with large file lists

### Test Case 3: File Navigation - Sequential

**Objective**: Verify Next/Previous button navigation works correctly.

**Steps**:
1. Open a PR with at least 5 files (from Test Case 2)
2. Tap "Next" button repeatedly until reaching the last file
3. Tap "Previous" button repeatedly until returning to the first file

**Expected Results**:
- **Each Next tap advances to the next file**
- File counter updates correctly (File 2 of X, File 3 of X, etc.)
- Highlighted file in list updates to match current file
- At last file:
  - Next button becomes disabled
  - Previous button remains enabled
- **Each Previous tap goes back to previous file**
- At first file:
  - Previous button becomes disabled
  - Next button remains enabled
- File information updates for each file

**Pass Criteria**:
- [ ] Can navigate forward through all files
- [ ] Can navigate backward through all files
- [ ] Buttons enable/disable correctly at boundaries
- [ ] File highlighting follows current file
- [ ] No skipped or duplicate files

### Test Case 4: File Navigation - Jump to File

**Objective**: Verify clicking on a file in the list jumps directly to that file.

**Steps**:
1. Open a PR with at least 5 files
2. Currently at File 1
3. Click on File 4 in the file list
4. Click on File 2 in the file list
5. Click on the last file in the file list

**Expected Results**:
- **Clicking File 4**: 
  - Current file becomes File 4
  - Counter shows "File 4 of X"
  - File 4 is highlighted in list
  - Both Previous and Next buttons enabled (if not at boundaries)
- **Clicking File 2**:
  - Jumps back to File 2
  - Counter shows "File 2 of X"
- **Clicking last file**:
  - Jumps to last file
  - Next button becomes disabled
  - Previous button enabled

**Pass Criteria**:
- [ ] Can jump to any file by clicking it
- [ ] Navigation state updates correctly
- [ ] No need to scroll through files sequentially
- [ ] Smooth UX with immediate response

### Test Case 5: Error Handling - Network Error

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

### Test Case 6: Error Handling - Invalid PR

**Objective**: Verify the app handles invalid/deleted PRs.

**Steps**:
1. Navigate to Current Work screen
2. Try to open a PR that has been deleted or doesn't exist

**Expected Results**:
- Error message displays
- Message indicates PR not found or access denied
- App does not crash
- User can navigate back

**Pass Criteria**:
- [ ] Appropriate error message
- [ ] App remains stable
- [ ] Can return to previous screen

### Test Case 7: Data Verification

**Objective**: Verify displayed data matches GitHub web interface.

**Steps**:
1. Open a specific PR in the app (note the PR number)
2. Open the same PR in GitHub web interface
3. Compare the following data points:

**Data to Verify**:
- PR number
- PR title
- PR description/body
- Author name and avatar
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

### Test Case 8: Edge Case - PR with No Files

**Objective**: Verify handling of PRs with no changed files (rare edge case).

**Note**: This is difficult to test manually as GitHub typically doesn't allow PRs with no changes. This scenario is covered by automated tests.

**Expected Results** (if encountered):
- PR metadata displays
- File list is empty or shows "No files changed"
- No crashes
- Navigation buttons are disabled

### Test Case 9: UI/UX Verification

**Objective**: Verify the overall user experience is smooth and intuitive.

**Steps**:
1. Open several different PRs
2. Navigate through files
3. Observe animations, transitions, and responsiveness

**Expected Results**:
- UI is responsive (no lag)
- File highlighting is clear and visible
- Button states are visually distinct (enabled vs disabled)
- Touch targets are adequate (48dp minimum)
- Text is readable
- Layout adapts to different screen sizes
- Scrolling is smooth

**Pass Criteria**:
- [ ] No performance issues
- [ ] Clear visual feedback
- [ ] Intuitive navigation
- [ ] Professional appearance

## Test Execution Checklist

### Automated Tests
- [x] PRReviewViewModelTest - All tests passing
- [x] PRReviewIntegrationTest - All tests passing
- [x] FileNavigationButtonsTest - All tests passing

### Manual Tests
- [ ] Test Case 1: PR with Single File
- [ ] Test Case 2: PR with Many Files
- [ ] Test Case 3: File Navigation - Sequential
- [ ] Test Case 4: File Navigation - Jump to File
- [ ] Test Case 5: Error Handling - Network Error
- [ ] Test Case 6: Error Handling - Invalid PR
- [ ] Test Case 7: Data Verification
- [ ] Test Case 8: Edge Case - PR with No Files (automated)
- [ ] Test Case 9: UI/UX Verification

## Known Limitations

1. **No Diff Viewer**: Phase 2 focuses on file navigation. Diff viewing is planned for Phase 3.
2. **No Gestures**: Gesture-based navigation is planned for Phase 5.
3. **No Comments**: Comment viewing/adding is planned for future phases.
4. **Limited Offline Support**: App requires network connection to load PRs.

## Test Environment

**Recommended Test Repositories**:
- Repository with variety of PR sizes (1 file, 5 files, 10+ files)
- Repository with different file types (.kt, .java, .xml, .md)
- Repository with PRs in different states (open, closed, merged)

**Devices to Test**:
- Minimum: Android 14 emulator (API 34)
- Recommended: Physical device with Android 14+
- Screen sizes: Phone (small), Tablet (large)

## Reporting Issues

If any test case fails, report with:
1. Test case number and name
2. Steps to reproduce
3. Expected vs actual result
4. Screenshots if UI-related
5. Device/emulator info
6. App version/commit hash

## References

- **Architecture**: `doc/ARCHITECTURE.md`
- **TODO**: `TODO.md` (Phase 2, Task 2.5)
- **Testing Guide**: `doc/TESTING.md`
- **Source Code**: 
  - `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/`
  - `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/`

---

**Document Version**: 1.0  
**Last Updated**: Phase 2, Task 2.5 completion  
**Status**: Ready for validation
