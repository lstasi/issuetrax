# Diff Viewer Feature - Validation Guide (Phase 3.4)

This document provides comprehensive validation procedures for the Diff Viewer feature (Phase 3, Task 3.4).

## Overview

The Diff Viewer feature allows users to:
- View inline diffs of code changes in pull requests
- See line-by-line additions, deletions, and context
- Navigate through code hunks within a file
- Toggle between expanded and collapsed views for large hunks
- Handle various file types and diff sizes
- View diffs optimized for mobile screens

## Automated Test Coverage

Comprehensive automated tests are located in:
- `app/src/test/java/com/issuetrax/app/domain/util/DiffParserTest.kt` (15 tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/components/DiffViewTest.kt` (10 tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/components/DiffLineTest.kt` (7 tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/components/DiffHunkTest.kt` (7 tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/components/InlineDiffViewTest.kt` (13 tests)
- `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/DiffViewerValidationTest.kt` (30 tests - Phase 3.4)

### Test Coverage Summary

✅ **Small Diffs (1-5 lines)**
- Single line additions
- Single line deletions
- Single line modifications
- Multiple small changes
- Empty line changes

✅ **Large Diffs (100+ lines)**
- 100+ line additions
- 150+ line mixed changes
- Multiple large hunks
- Performance testing with complex structures

✅ **Various File Types**
- Kotlin source code (.kt)
- Java source code (.java)
- XML layouts (.xml)
- JSON configuration (.json)
- Markdown documentation (.md)
- Gradle build scripts (.gradle.kts)
- Properties files (.properties)

✅ **Mobile Readability**
- Long line handling (150+ characters)
- Deep indentation levels
- Special characters and emojis
- Tab vs space indentation

✅ **Edge Cases**
- No newline at end of file
- Binary file detection
- Whitespace-only changes
- Empty file creation
- File deletion
- Multiple hunks per file

## Manual Validation Procedures

### Prerequisites

1. Android device or emulator with API 34+ (Android 14)
2. GitHub Personal Access Token configured in the app
3. Access to a repository with various types of PRs containing different diff sizes
4. Test repository recommendations:
   - Small PRs (1-5 changed lines per file)
   - Large PRs (100+ changed lines per file)
   - PRs with various file types

---

## Test Case 1: Small Diff - Single Line Addition

**Objective**: Verify the app correctly displays a small diff with a single added line.

**Test PR**: Find or create a PR with a file that has exactly 1 added line.

**Steps**:
1. Navigate to Current Work screen
2. Select a PR with a small, single-line addition
3. Tap on the PR to open PR Review screen
4. Select a file with 1 added line
5. View the diff display

**Expected Results**:
- Diff shows the context lines (unchanged)
- Added line is highlighted with **green background**
- Added line shows **"+" prefix** or equivalent indicator
- Line numbers are displayed correctly:
  - Old line numbers for context/deletions
  - New line numbers for context/additions
- Code is displayed in **monospace font**
- Indentation is preserved
- The diff is readable on mobile screen

**Pass Criteria**:
- [ ] Green highlighting visible and clear
- [ ] Line numbers accurate
- [ ] Text is readable
- [ ] Indentation preserved
- [ ] No horizontal scrolling needed for short lines

---

## Test Case 2: Small Diff - Single Line Deletion

**Objective**: Verify the app correctly displays a small diff with a single deleted line.

**Test PR**: Find or create a PR with a file that has exactly 1 deleted line.

**Steps**:
1. Open a PR with single-line deletion
2. Navigate to the file in PR Review screen
3. View the diff display

**Expected Results**:
- Deleted line is highlighted with **red background**
- Deleted line shows **"-" prefix** or equivalent indicator
- Context lines are displayed with neutral/gray coloring
- Line numbers show old line number for deletion

**Pass Criteria**:
- [ ] Red highlighting visible and clear
- [ ] Deletion clearly distinguishable from context
- [ ] Line numbers accurate
- [ ] Text is readable

---

## Test Case 3: Small Diff - Line Modification

**Objective**: Verify the app correctly displays a line change (deletion + addition).

**Test PR**: Find a PR where a line is modified (shows as 1 deletion + 1 addition).

**Steps**:
1. Open a PR with line modifications
2. Navigate to the file with modified lines
3. View the diff display

**Expected Results**:
- Old version shows with red background (deletion)
- New version shows with green background (addition)
- Lines are displayed sequentially (deletion before addition)
- Easy to compare old vs new
- Line numbers reflect the change

**Pass Criteria**:
- [ ] Both old and new versions visible
- [ ] Clear visual distinction between deletion and addition
- [ ] Easy to spot the actual change
- [ ] Readable on mobile screen

---

## Test Case 4: Small Diff - Multiple Changes (3-5 lines)

**Objective**: Verify handling of small diffs with multiple changes.

**Test PR**: Find a PR with 3-5 changed lines in a single file.

**Steps**:
1. Open a PR with 3-5 line changes
2. Navigate to the file
3. Observe the diff display

**Expected Results**:
- All changes are visible
- Context lines separate different changes
- Each change has correct color coding
- Line numbering is sequential and accurate
- The diff is not overwhelming on mobile screen

**Pass Criteria**:
- [ ] All changes clearly visible
- [ ] Context lines provide sufficient context
- [ ] Color coding is consistent
- [ ] Easy to scan through changes

---

## Test Case 5: Large Diff - 50+ Line Changes

**Objective**: Verify the app handles large diffs efficiently.

**Test PR**: Find a PR with a file that has 50+ changed lines.

**Steps**:
1. Open a PR with large changes (50+ lines)
2. Navigate to a large file
3. Scroll through the diff
4. Observe performance and usability

**Expected Results**:
- Diff loads without significant delay (< 2 seconds)
- Smooth scrolling through large diffs
- All lines are rendered correctly
- Line numbers remain visible while scrolling
- No UI lag or stuttering
- Large hunks may be collapsed by default (if > 10 lines)
- Option to expand/collapse large hunks

**Pass Criteria**:
- [ ] Fast loading time
- [ ] Smooth scrolling performance
- [ ] No memory issues
- [ ] Readable despite large size
- [ ] Expand/collapse functionality works

---

## Test Case 6: Large Diff - 100+ Line Changes

**Objective**: Verify extreme large diff handling.

**Test PR**: Find a PR with 100+ changed lines in a single file.

**Steps**:
1. Open a PR with very large changes (100+ lines)
2. Navigate to the large file
3. Test scrolling and navigation
4. Monitor app performance

**Expected Results**:
- App remains responsive
- Diff renders completely
- Virtual scrolling or lazy loading may be used
- Memory usage stays reasonable
- Large hunks are collapsed by default
- Can expand individual hunks as needed

**Pass Criteria**:
- [ ] No crashes
- [ ] Acceptable performance
- [ ] All content accessible
- [ ] Memory efficient
- [ ] Usable on mobile device

---

## Test Case 7: Multiple Hunks in Single File

**Objective**: Verify handling of files with multiple separate change sections (hunks).

**Test PR**: Find a file with changes in multiple sections (e.g., line 10 and line 100).

**Steps**:
1. Open a PR with a file containing multiple hunks
2. Navigate to the file
3. Scroll through all hunks
4. Observe hunk separation

**Expected Results**:
- Each hunk is clearly separated
- Hunk headers show line number ranges
- Can identify which part of file each hunk represents
- Context between hunks is indicated (e.g., "... 50 lines unchanged ...")
- Each hunk can be expanded/collapsed independently

**Pass Criteria**:
- [ ] Clear hunk separation
- [ ] Hunk headers informative
- [ ] Easy to navigate between hunks
- [ ] Independent expand/collapse per hunk

---

## Test Case 8: Various File Types - Kotlin

**Objective**: Verify Kotlin source code displays correctly.

**Test PR**: Find a PR with changes to .kt files.

**Steps**:
1. Open a PR with Kotlin file changes
2. View various Kotlin constructs (classes, functions, data classes, etc.)
3. Check diff rendering

**Expected Results**:
- Kotlin syntax preserved (no corruption)
- Indentation correct
- Special characters preserved (?, !, :, etc.)
- String literals displayed correctly
- Comments rendered properly
- Lambda expressions readable

**Pass Criteria**:
- [ ] Kotlin code readable
- [ ] Syntax intact
- [ ] Indentation preserved
- [ ] No character corruption

---

## Test Case 9: Various File Types - XML

**Objective**: Verify XML layout files display correctly.

**Test PR**: Find a PR with changes to .xml files.

**Steps**:
1. Open a PR with XML file changes
2. View XML attributes and tags
3. Check diff rendering

**Expected Results**:
- XML tags displayed correctly (< and > preserved)
- Attributes readable
- Nested structure clear
- Quotes and special characters intact
- Indentation shows hierarchy

**Pass Criteria**:
- [ ] XML structure clear
- [ ] Tags and attributes readable
- [ ] Hierarchy visible
- [ ] No character escaping issues

---

## Test Case 10: Various File Types - JSON

**Objective**: Verify JSON configuration files display correctly.

**Test PR**: Find a PR with changes to .json files.

**Steps**:
1. Open a PR with JSON file changes
2. View JSON structure
3. Check diff rendering

**Expected Results**:
- JSON structure preserved
- Braces and brackets aligned
- Quotes displayed correctly
- Nested objects readable
- Arrays formatted properly

**Pass Criteria**:
- [ ] JSON structure clear
- [ ] Valid syntax preserved
- [ ] Nested levels distinguishable
- [ ] Readable formatting

---

## Test Case 11: Various File Types - Markdown

**Objective**: Verify Markdown documentation displays correctly.

**Test PR**: Find a PR with changes to .md files.

**Steps**:
1. Open a PR with Markdown file changes
2. View headers, lists, code blocks
3. Check diff rendering

**Expected Results**:
- Markdown syntax visible (# for headers, - for lists, etc.)
- Code blocks preserved
- Links displayed correctly
- Special characters intact

**Pass Criteria**:
- [ ] Markdown syntax clear
- [ ] Headers, lists, code blocks readable
- [ ] Links and references intact

---

## Test Case 12: Mobile Readability - Long Lines

**Objective**: Verify handling of very long lines that exceed mobile screen width.

**Test PR**: Find a file with lines > 100 characters.

**Steps**:
1. Open a PR with long lines of code
2. View the diff
3. Test horizontal scrolling

**Expected Results**:
- Long lines don't break UI layout
- Horizontal scrolling available for long lines
- Line numbers remain visible while scrolling horizontally
- Scrolling is smooth
- Can view entire line content

**Pass Criteria**:
- [ ] UI layout intact
- [ ] Horizontal scroll works
- [ ] Line numbers stay visible
- [ ] Full content accessible
- [ ] Smooth scrolling

---

## Test Case 13: Mobile Readability - Deep Indentation

**Objective**: Verify handling of deeply nested code (many indentation levels).

**Test PR**: Find code with 4+ levels of nesting.

**Steps**:
1. Open a PR with deeply nested code
2. View the diff
3. Check indentation rendering

**Expected Results**:
- Indentation preserved accurately
- Nested structure clear
- No indentation overflow issues
- Readable despite deep nesting
- May require horizontal scroll (acceptable)

**Pass Criteria**:
- [ ] Indentation accurate
- [ ] Structure comprehensible
- [ ] No layout breaking
- [ ] Scrolling available if needed

---

## Test Case 14: Mobile Readability - Special Characters

**Objective**: Verify special characters, symbols, and emojis display correctly.

**Test PR**: Find code with special characters (€, ©, ™, 👋, ✅, etc.).

**Steps**:
1. Open a PR with special characters
2. View the diff
3. Verify character rendering

**Expected Results**:
- All characters display correctly
- No replacement with � or boxes
- Emojis render properly
- Unicode characters intact
- Regex patterns readable

**Pass Criteria**:
- [ ] Special characters render
- [ ] Emojis display (if present)
- [ ] No character corruption
- [ ] Readable text

---

## Test Case 15: Edge Case - No Newline at End of File

**Objective**: Verify handling of "No newline at end of file" marker.

**Test PR**: Find or create a file change that triggers this marker.

**Steps**:
1. Open a PR with "No newline at end of file"
2. View the affected file
3. Check marker display

**Expected Results**:
- Marker is displayed clearly
- Indicated with "\ No newline at end of file" or equivalent
- Does not confuse with regular code
- Positioned correctly at end of hunk

**Pass Criteria**:
- [ ] Marker visible
- [ ] Clearly distinguished from code
- [ ] Properly positioned
- [ ] Not confusing

---

## Test Case 16: Edge Case - Binary File

**Objective**: Verify binary files are handled gracefully.

**Test PR**: Find a PR with binary file changes (images, .so files, etc.).

**Steps**:
1. Open a PR with binary file changes
2. Navigate to a binary file
3. Check what is displayed

**Expected Results**:
- Message indicates "Binary file" or "Cannot display diff"
- No attempt to render gibberish
- File metadata still visible (additions/deletions counts may be 0)
- Graceful handling without errors

**Pass Criteria**:
- [ ] Clear message displayed
- [ ] No error state
- [ ] Metadata accessible
- [ ] Professional appearance

---

## Test Case 17: Edge Case - Empty File Creation

**Objective**: Verify handling of creating an empty file.

**Test PR**: Find a PR that creates an empty file.

**Steps**:
1. Open a PR with empty file creation
2. Navigate to the empty file
3. Check diff display

**Expected Results**:
- File is listed in changed files
- Diff shows as empty or "File created (empty)"
- No errors or crashes
- Status shows "ADDED"

**Pass Criteria**:
- [ ] File listed
- [ ] Appropriate message or empty diff
- [ ] No errors
- [ ] Correct status

---

## Test Case 18: Edge Case - File Deletion

**Objective**: Verify handling of deleted files.

**Test PR**: Find a PR that deletes files.

**Steps**:
1. Open a PR with file deletions
2. Navigate to a deleted file
3. View the diff

**Expected Results**:
- All lines shown with red background (deletions)
- Status shows "REMOVED"
- File metadata indicates deletion
- Content of deleted file is viewable

**Pass Criteria**:
- [ ] All lines show as deletions
- [ ] Status accurate
- [ ] Content accessible
- [ ] Clear indication of deletion

---

## Test Case 19: Edge Case - Whitespace-Only Changes

**Objective**: Verify handling of changes with only whitespace differences.

**Test PR**: Find changes with trailing spaces, tab-to-space conversions, etc.

**Steps**:
1. Open a PR with whitespace changes
2. View the diff
3. Check if changes are visible

**Expected Results**:
- Changes are detected and shown
- Whitespace differences may be subtle
- Lines marked as changed even if visually similar
- May have visual indicators for whitespace

**Pass Criteria**:
- [ ] Changes detected
- [ ] Marked as additions/deletions
- [ ] User can see something changed
- [ ] No confusion

---

## Test Case 20: Toggle View Mode (Inline vs Standard)

**Objective**: Verify view mode toggle functionality.

**Test PR**: Any PR with diffs.

**Steps**:
1. Open a PR and view a file diff
2. Locate the view mode toggle (if implemented)
3. Toggle between Inline and Standard views
4. Compare both views

**Expected Results**:
- Toggle button is accessible and clear
- Switching between modes is immediate
- Both modes display the same data
- Inline mode: additions/deletions interleaved
- Standard mode: may show in parallel or expanded format
- User preference is saved (optional)

**Pass Criteria**:
- [ ] Toggle works smoothly
- [ ] Both modes functional
- [ ] Clear visual difference
- [ ] Data consistency

---

## Test Case 21: Performance - Large File Scrolling

**Objective**: Verify scrolling performance with large diffs.

**Test PR**: PR with 500+ line file change.

**Steps**:
1. Open a very large diff (500+ lines)
2. Scroll rapidly from top to bottom
3. Scroll back up
4. Monitor frame rate and smoothness

**Expected Results**:
- Smooth scrolling (60 FPS or close)
- No visible lag or stuttering
- Line numbers update smoothly
- App remains responsive
- No memory warnings

**Pass Criteria**:
- [ ] Smooth scrolling experience
- [ ] No lag or jank
- [ ] App responsive
- [ ] No crashes

---

## Test Case 22: Comparison with GitHub Web Interface

**Objective**: Verify data accuracy by comparing with GitHub's web interface.

**Test PR**: Any PR with mixed changes.

**Steps**:
1. Select a specific PR in the app (note the PR number)
2. Open the same PR in GitHub web interface
3. Compare the following for a specific file:

**Data Points to Verify**:
- Number of additions
- Number of deletions
- Number of hunks
- Line numbers in hunks
- Content of additions/deletions
- Context lines
- Hunk headers

**Expected Results**:
- Addition/deletion counts match exactly
- Line numbers are identical
- Content is character-for-character identical
- Hunk structure matches
- No missing or extra lines

**Pass Criteria**:
- [ ] Addition counts match
- [ ] Deletion counts match
- [ ] Line numbers accurate
- [ ] Content identical
- [ ] Hunk structure correct

---

## Test Case 23: UI/UX - Color Coding Clarity

**Objective**: Verify color coding is clear and accessible.

**Test PR**: Any PR with mixed changes.

**Steps**:
1. View various diffs
2. Observe color scheme
3. Test in both light and dark mode (if supported)

**Expected Results**:
- Additions clearly distinguishable (green)
- Deletions clearly distinguishable (red)
- Context lines neutral (gray/white)
- Sufficient contrast for readability
- Colors work in both light and dark themes
- Accessible to users with color vision deficiencies

**Pass Criteria**:
- [ ] Clear color distinction
- [ ] Good contrast
- [ ] Works in both themes
- [ ] Accessible colors

---

## Test Case 24: UI/UX - Touch Targets

**Objective**: Verify interactive elements have adequate touch targets.

**Test PR**: Any PR with collapsible hunks.

**Steps**:
1. View a file with collapsible hunks
2. Attempt to tap expand/collapse buttons
3. Check ease of interaction

**Expected Results**:
- Expand/collapse buttons are large enough (48dp minimum)
- Easy to tap without mistakes
- Clear visual feedback on tap
- Buttons don't overlap with other content

**Pass Criteria**:
- [ ] Touch targets adequate size
- [ ] Easy to tap
- [ ] Visual feedback present
- [ ] No overlap issues

---

## Test Case 25: Error Handling - Network Error During Diff Load

**Objective**: Verify graceful handling of network errors while loading diffs.

**Test PR**: Any PR.

**Steps**:
1. Open a PR
2. Enable airplane mode or disconnect network
3. Try to view a file diff that hasn't been loaded yet

**Expected Results**:
- Error message displays
- Message is clear and helpful
- Error text is selectable (wrapped in SelectionContainer)
- Retry option available
- App does not crash
- Can navigate back

**Pass Criteria**:
- [ ] Error message displays
- [ ] Text is selectable
- [ ] Retry option available
- [ ] No crash
- [ ] Can navigate away

---

## Performance Benchmarks

### Expected Performance Metrics

| Scenario | Expected Time | Acceptable Range |
|----------|--------------|------------------|
| Small diff load (< 10 lines) | < 100ms | 0-200ms |
| Medium diff load (10-50 lines) | < 300ms | 0-500ms |
| Large diff load (50-200 lines) | < 800ms | 0-1500ms |
| Very large diff load (200+ lines) | < 2000ms | 0-3000ms |
| Scrolling frame rate | 60 FPS | 50-60 FPS |
| Memory usage (typical) | < 100MB | 50-150MB |

### Performance Test Procedure

1. Use Android Studio Profiler to monitor:
   - CPU usage
   - Memory allocation
   - Frame rendering time
2. Test with various diff sizes
3. Record results and compare with benchmarks
4. Identify any performance bottlenecks

---

## Test Execution Checklist

### Automated Tests (all passing)
- [x] DiffParserTest (15 tests) - ✅ PASSING
- [x] DiffViewTest (9 tests) - ✅ PASSING
- [x] DiffLineTest (7 tests) - ✅ PASSING
- [x] DiffHunkTest (7 tests) - ✅ PASSING
- [x] InlineDiffViewTest (13 tests) - ✅ PASSING
- [x] DiffViewerValidationTest (25 tests) - ✅ PASSING

### Manual Tests (Phase 3.4 Requirements)
- [ ] Test Case 1: Small Diff - Single Line Addition
- [ ] Test Case 2: Small Diff - Single Line Deletion
- [ ] Test Case 3: Small Diff - Line Modification
- [ ] Test Case 4: Small Diff - Multiple Changes (3-5 lines) ✅ **Required**
- [ ] Test Case 5: Large Diff - 50+ Line Changes
- [ ] Test Case 6: Large Diff - 100+ Line Changes ✅ **Required**
- [ ] Test Case 7: Multiple Hunks in Single File
- [ ] Test Case 8: Various File Types - Kotlin ✅ **Required**
- [ ] Test Case 9: Various File Types - XML ✅ **Required**
- [ ] Test Case 10: Various File Types - JSON ✅ **Required**
- [ ] Test Case 11: Various File Types - Markdown ✅ **Required**
- [ ] Test Case 12: Mobile Readability - Long Lines ✅ **Required**
- [ ] Test Case 13: Mobile Readability - Deep Indentation ✅ **Required**
- [ ] Test Case 14: Mobile Readability - Special Characters
- [ ] Test Case 15: Edge Case - No Newline at End of File
- [ ] Test Case 16: Edge Case - Binary File
- [ ] Test Case 17: Edge Case - Empty File Creation
- [ ] Test Case 18: Edge Case - File Deletion
- [ ] Test Case 19: Edge Case - Whitespace-Only Changes
- [ ] Test Case 20: Toggle View Mode (Inline vs Standard)
- [ ] Test Case 21: Performance - Large File Scrolling
- [ ] Test Case 22: Comparison with GitHub Web Interface ✅ **Required**
- [ ] Test Case 23: UI/UX - Color Coding Clarity
- [ ] Test Case 24: UI/UX - Touch Targets
- [ ] Test Case 25: Error Handling - Network Error During Diff Load

---

## Known Limitations

1. **Syntax Highlighting**: Phase 3 does not include syntax highlighting. This is planned for Phase 6.
2. **Side-by-Side View**: Only inline view is implemented in Phase 3. Side-by-side is not planned.
3. **Inline Comments**: Phase 3 focuses on viewing diffs. Comments are planned for Phase 7.
4. **Gesture Navigation**: Planned for Phase 5.

---

## Test Environment

**Recommended Test Repositories**:
- Repository with small PRs (1-5 line changes)
- Repository with large PRs (100+ line changes)
- Repository with various file types (.kt, .java, .xml, .json, .md, .gradle)
- Repository with edge cases (binary files, empty files, deletions)

**Devices to Test**:
- Minimum: Android 14 emulator (API 34)
- Recommended: Physical device with Android 14+
- Screen sizes: Phone (small 5-6"), Phablet (6-7"), Tablet

**Test Accounts**:
- GitHub account with PAT that has `repo` scope
- Access to repositories with diverse PR scenarios

---

## Reporting Issues

If any test case fails, report with:
1. Test case number and name
2. Steps to reproduce
3. Expected vs actual result
4. Screenshots (if UI-related)
5. Device/emulator information
6. App version/commit hash
7. PR and file being tested (GitHub URL)

---

## References

- **Architecture**: `doc/ARCHITECTURE.md`
- **TODO**: `TODO.md` (Phase 3, Task 3.4)
- **Source Code**: 
  - `app/src/main/java/com/issuetrax/app/domain/util/DiffParser.kt`
  - `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/DiffView.kt`
  - `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/DiffLine.kt`
  - `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/DiffHunk.kt`
  - `app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/InlineDiffView.kt`
- **Test Files**:
  - `app/src/test/java/com/issuetrax/app/presentation/ui/pr_review/DiffViewerValidationTest.kt`

---

**Document Version**: 1.0  
**Last Updated**: Phase 3, Task 3.4 completion  
**Status**: Ready for validation
