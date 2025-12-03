# PR Review Interface - Icon Improvement Proposal

## Executive Summary

This document proposes improvements to the icons used in the PR review interface to make them more intuitive, semantically meaningful, and aligned with GitHub's design language. The current implementation uses basic Material Design icons that sometimes lack clarity about their purpose.

## Current Icon Analysis

### 1. PRActionToolbar Icons

| Action | Current Icon | Issue | Proposed Icon | Rationale |
|--------|--------------|-------|---------------|-----------|
| **View PR Description** | `Icons.Default.Info` | ✅ Good | Keep as-is | Info icon clearly indicates information/description |
| **Merge PR** | `Icons.Default.Add` | ❌ Poor | `Icons.Filled.MergeType` or `Icons.Filled.CallMerge` | Add icon is confusing - doesn't convey merging. GitHub uses a merge/branch icon |
| **Approve PR** | `Icons.Default.CheckCircle` | ✅ Good | Keep as-is | Check circle clearly indicates approval/success |
| **Re-run Workflow** | `Icons.Default.PlayArrow` | ✅ Good | Keep as-is | Play arrow universally means "run" or "execute" |
| **Add Comment** | `Icons.Default.Edit` | ⚠️ Unclear | `Icons.Filled.Comment` or `Icons.Filled.ChatBubble` | Edit suggests modifying existing content, not creating a comment |
| **Close PR** | `Icons.Default.Close` | ✅ Good | Keep as-is | X icon clearly means close/dismiss |

### 2. FileListView Icons

#### File Status Icons

| Status | Current Icon | Issue | Proposed Icon | Rationale |
|--------|--------------|-------|---------------|-----------|
| **File Added** | `Icons.Default.Add` | ✅ Good | Keep as-is | Plus sign clearly indicates addition |
| **File Modified** | `Icons.Default.Edit` | ⚠️ Generic | `Icons.Filled.Create` or custom diff icon | Edit is okay but could be more specific to code changes |
| **File Removed** | `Icons.Default.Close` | ⚠️ Unclear | `Icons.Filled.Delete` or `Icons.Filled.Remove` | Close/X can be confused with general dismissal |
| **File Renamed** | `Icons.Default.Edit` | ❌ Wrong | `Icons.Filled.DriveFileRenameOutline` or `Icons.Filled.Sync` | Edit doesn't convey renaming - needs arrow or sync icon |
| **File Copied** | `Icons.Default.Edit` | ❌ Wrong | `Icons.Filled.ContentCopy` or `Icons.Filled.FileCopy` | Edit doesn't convey copying at all |

#### Build Status Icons

| Status | Current Icon | Issue | Proposed Icon | Rationale |
|--------|--------------|-------|---------------|-----------|
| **Success** | `Icons.Default.CheckCircle` | ✅ Good | Keep as-is | Green check circle is universal for success |
| **Failure** | `Icons.Default.Close` | ⚠️ Unclear | `Icons.Filled.Cancel` or `Icons.Filled.Error` | Close/X could be more emphatic for errors |
| **Pending** | `Icons.Default.Refresh` | ✅ Good | `Icons.Filled.Schedule` or `Icons.Filled.HourglassEmpty` | Refresh works, but hourglass/clock might be clearer |
| **Error** | `Icons.Default.Close` | ⚠️ Unclear | `Icons.Filled.ErrorOutline` or `Icons.Filled.Warning` | Should distinguish between failure and error states |

### 3. FileNavigationButtons Icons

| Action | Current Icon | Issue | Proposed Icon | Rationale |
|--------|--------------|-------|---------------|-----------|
| **Previous File** | `Icons.Default.KeyboardArrowLeft` | ✅ Good | Keep as-is | Left arrow is universal for "previous" |
| **Next File** | `Icons.Default.KeyboardArrowRight` | ✅ Good | Keep as-is | Right arrow is universal for "next" |

### 4. HunkDetailView Icons

| Action | Current Icon | Issue | Proposed Icon | Rationale |
|--------|--------------|-------|---------------|-----------|
| **Close Detail View** | `Icons.Default.Close` | ✅ Good | Keep as-is | X icon clearly means close/exit |

## Material Icons Availability

Material Icons provides several relevant icons that are NOT currently used but would improve clarity:

### Available Icons for GitHub Actions

- `Icons.Filled.MergeType` - Perfect for merge operations
- `Icons.Filled.CallMerge` - Alternative merge icon
- `Icons.Filled.Comment` - Better for commenting
- `Icons.Filled.ChatBubble` - Alternative comment icon
- `Icons.Filled.Delete` - Clear deletion indicator
- `Icons.Filled.Remove` - Alternative removal icon
- `Icons.Filled.ContentCopy` - Copy operations
- `Icons.Filled.FileCopy` - File copying
- `Icons.Filled.DriveFileRenameOutline` - Rename operations
- `Icons.Filled.Sync` - Synchronization/rename
- `Icons.Filled.Error` - Error states
- `Icons.Filled.ErrorOutline` - Outlined error
- `Icons.Filled.Warning` - Warning states
- `Icons.Filled.Schedule` - Scheduled/pending
- `Icons.Filled.HourglassEmpty` - Waiting/pending

### Icons That May NOT Be Available (Need Verification)

Some GitHub-specific icons may not be in the standard Material Icons set:
- Branch/Git icons
- Pull request specific icons
- Commit icons
- Diff-specific icons

These would need to be:
1. Checked if they exist in Material Icons Extended
2. Created as custom SVG icons if needed
3. Or use closest Material Icons alternatives

## Recommended Implementation Plan

Based on icon availability and project philosophy (minimal dependencies), we have TWO approaches:

### Approach A: Use Only Core Icons (NO new dependencies)
This follows the project's "minimal dependencies" philosophy strictly.

**Changes without adding dependencies:**
1. **File Removed**: Change from `Close` to `Delete` ✅ (core icon, clearer)
2. **Build Failure/Error**: Change from `Close` to `Error` ✅ (core icon, more specific)
3. **File Modified**: Keep `Edit` but add better content description
4. **Merge button**: Keep `Add` but consider changing to `Send` ✅ (core icon, better semantics)
5. **Comment button**: Keep `Edit` but add clearer content description

**Result**: Modest improvement using only existing dependencies

### Approach B: Add Extended Icons (Requires dependency)
This adds `material-icons-extended` (~4MB) but provides better UX.

**High Priority Changes (requires extended icons):**
1. **Merge button**: Change from `Add` to `CallMerge` 🔧 (requires extended)
2. **Comment button**: Change from `Edit` to `Comment` 🔧 (requires extended)
3. **File Removed**: Change from `Close` to `Delete` ✅ (core icon)
4. **File Renamed**: Change from `Edit` to `DriveFileRenameOutline` or `Sync` 🔧 (requires extended)
5. **File Copied**: Change from `Edit` to `ContentCopy` 🔧 (requires extended)

**Medium Priority Changes:**
6. **Build Failure**: Change from `Close` to `Error` ✅ (core icon)
7. **Build Pending**: Consider `Schedule` 🔧 (requires extended)

**Result**: Significant UX improvement but adds 4MB to APK size

### RECOMMENDATION: Modified Approach B

Add `material-icons-extended` but **only for critical improvements**, then document which icons are used for future optimization (ProGuard can remove unused icons from extended package).

**Priority implementation order:**
1. Add `material-icons-extended` dependency
2. Change File Removed: `Close` → `Delete` (use core)
3. Change Build Failure: `Close` → `Error` (use core)
4. Change Merge button: `Add` → `CallMerge` (use extended)
5. Change Comment button: `Edit` → `Comment` (use extended)
6. Change File Renamed: `Edit` → `DriveFileRenameOutline` (use extended)
7. Change File Copied: `Edit` → `ContentCopy` (use extended)

## Icon Availability Research Results

Based on Material Icons documentation, the icons are categorized as follows:

### Available in material-icons-core (Already in project)
These icons require NO additional dependencies:
- `Icons.Filled.Add` ✅
- `Icons.Filled.CheckCircle` ✅
- `Icons.Filled.Close` ✅
- `Icons.Filled.Edit` ✅
- `Icons.Filled.Info` ✅
- `Icons.Filled.Delete` ✅
- `Icons.Filled.Error` ✅
- `Icons.Filled.Warning` ✅
- `Icons.Filled.Send` ✅
- `Icons.Filled.Done` ✅

### Available ONLY in material-icons-extended (Requires dependency)
These icons need the extended package:
- `Icons.Filled.CallMerge` (for merge action)
- `Icons.Filled.Comment` (for comment action)
- `Icons.Filled.ChatBubble` (alternative comment)
- `Icons.Filled.ContentCopy` (for file copy)
- `Icons.Filled.DriveFileRenameOutline` (for file rename)
- `Icons.Filled.Sync` (alternative rename indicator)
- `Icons.Filled.Schedule` (for pending status)

### NOT Available (Need alternatives or custom icons)
- `Icons.Filled.MergeType` - Does not exist, use `CallMerge` instead
- `Icons.Filled.FileCopy` - Does not exist, use `ContentCopy` instead
- `Icons.Filled.Remove` - Does not exist, use `Delete` instead
- `Icons.Filled.HourglassEmpty` - May not exist, use `Schedule` instead

## Alternative: Material Symbols or Custom Icons

If certain icons are not available in the basic Material Icons package, we have options:

1. **Material Icons Extended**: Larger icon set (but increases APK size)
2. **Material Symbols**: Google's newer icon system
3. **Custom SVG Icons**: Create GitHub-specific icons
4. **Icon Font**: Use GitHub's Octicons as a font
5. **Compose Painter**: Create custom vector drawables

## Design Guidelines

### Icon Consistency
- Use filled icons for primary actions
- Use outlined icons for secondary/less important actions
- Maintain consistent icon size (24dp standard, 20dp for compact)

### Color Coding
- **Green/Primary**: Success, approve, add
- **Red/Error**: Delete, close, failure
- **Blue/Secondary**: Info, neutral actions
- **Yellow/Warning**: Caution, pending
- **Purple/Tertiary**: Modified, changed

### Accessibility
- All icons must have meaningful `contentDescription`
- Icons should be accompanied by text labels where space permits
- Color should not be the only indicator (use shape/icon differences)
- Ensure sufficient contrast ratios

## Implementation Checklist

- [ ] Verify which Material Icons are available in current dependencies
- [ ] Test import statements for all proposed icons
- [ ] If icons are missing, evaluate whether to:
  - [ ] Add Material Icons Extended dependency
  - [ ] Create custom SVG icons
  - [ ] Use alternative available icons
- [ ] Update PRActionToolbar component
- [ ] Update FileListView component
- [ ] Update icon tests (if any test icon appearance)
- [ ] Add proper content descriptions to all new icons
- [ ] Document icon choices in code comments
- [ ] Take before/after screenshots for review
- [ ] Validate accessibility with TalkBack

## Semantic Meaning Reference

### GitHub Action Semantics
- **Merge**: Combining branches (not addition)
- **Approve**: Positive review (check/thumbs up)
- **Comment**: Communication (speech bubble)
- **Close**: Ending without merge (X or lock)
- **Re-run**: Execute again (play/refresh)

### File Operation Semantics
- **Add**: New file creation (+)
- **Modify**: Content changes (pencil/edit)
- **Delete**: File removal (trash/minus)
- **Rename**: Name change (arrows/sync)
- **Copy**: Duplication (copy icon)

### Status Semantics
- **Success**: Completed successfully (check)
- **Failure**: Failed execution (X/error)
- **Pending**: In progress (clock/spinner)
- **Error**: System error (warning triangle)

## Conclusion

Improving the icons in the PR review interface will significantly enhance user experience by making actions more intuitive and reducing cognitive load. The most critical changes are:

1. Using a merge icon instead of add for PR merging
2. Using a comment icon instead of edit for commenting
3. Using delete/remove icons instead of close for file deletion
4. Using appropriate icons for rename and copy operations

These changes align with GitHub's design language and industry standards for code review interfaces.
