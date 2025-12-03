# Icon Quick Reference Card

Quick reference for PR review interface icons - current state and proposed improvements.

## PRActionToolbar

| Button | Current Icon | Proposed Icon | Change Type | Dependency |
|--------|--------------|---------------|-------------|------------|
| Info | `Info` ℹ️ | `Info` ℹ️ | ✅ Keep | Core |
| Merge | `Add` ➕ | `CallMerge` 🔀 | 🎯 Critical | Extended |
| Approve | `CheckCircle` ✅ | `CheckCircle` ✅ | ✅ Keep | Core |
| Re-run | `PlayArrow` ▶️ | `PlayArrow` ▶️ | ✅ Keep | Core |
| Comment | `Edit` ✏️ | `Comment` 💬 | 🎯 Critical | Extended |
| Close | `Close` ❌ | `Close` ❌ | ✅ Keep | Core |

## FileListView - File Status

| Status | Current Icon | Proposed Icon | Change Type | Dependency |
|--------|--------------|---------------|-------------|------------|
| Added | `Add` ➕ | `Add` ➕ | ✅ Keep | Core |
| Modified | `Edit` ✏️ | `Edit` ✏️ | ✅ Keep | Core |
| Removed | `Close` ❌ | `Delete` 🗑️ | 📈 Important | Core |
| Renamed | `Edit` ✏️ | `DriveFileRenameOutline` 🔄 | 🎯 Critical | Extended |
| Copied | `Edit` ✏️ | `ContentCopy` 📋 | 🎯 Critical | Extended |
| Unchanged | `Edit` ✏️ | `Edit` ✏️ | ✅ Keep | Core |

## FileListView - Build Status

| Status | Current Icon | Proposed Icon | Change Type | Dependency |
|--------|--------------|---------------|-------------|------------|
| Success | `CheckCircle` ✅ | `CheckCircle` ✅ | ✅ Keep | Core |
| Failure | `Close` ❌ | `Error` ⚠️ | 📈 Important | Core |
| Pending | `Refresh` 🔄 | `Refresh` 🔄 | ✅ Keep | Core |
| Error | `Close` ❌ | `Error` ⚠️ | 📈 Important | Core |

## FileNavigationButtons

| Button | Current Icon | Proposed Icon | Change Type | Dependency |
|--------|--------------|---------------|-------------|------------|
| Previous | `KeyboardArrowLeft` ⬅️ | `KeyboardArrowLeft` ⬅️ | ✅ Keep | Core |
| Next | `KeyboardArrowRight` ➡️ | `KeyboardArrowRight` ➡️ | ✅ Keep | Core |

## HunkDetailView

| Button | Current Icon | Proposed Icon | Change Type | Dependency |
|--------|--------------|---------------|-------------|------------|
| Close | `Close` ❌ | `Close` ❌ | ✅ Keep | Core |

---

## Legend

- 🎯 **Critical**: Wrong semantic meaning - must fix
- 📈 **Important**: Unclear meaning - should fix
- ✅ **Keep**: Already good - no change
- **Core**: Available in material-icons-core (no dependency)
- **Extended**: Requires material-icons-extended (~4MB)

---

## Summary Statistics

- **Total icons**: 20
- **Keep as-is**: 14 (70%)
- **Need changes**: 6 (30%)
  - Critical: 4 (20%)
  - Important: 2 (10%)
- **Core icons**: 2 changes
- **Extended icons**: 4 changes

---

## Import Statements

### Current Imports (Core)
```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
```

### Additional Imports Needed (Option A - Core Only)
```kotlin
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
```

### Additional Imports Needed (Option B - Extended)
```kotlin
// Core icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error

// Extended icons
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DriveFileRenameOutline
```

---

## Code Changes Location

### PRActionToolbar.kt
- **Lines to change**: ~74, ~84, ~116, ~354, ~411
- **Changes**: Merge icon, Comment icon (2 locations each)

### FileListView.kt
- **Lines to change**: ~172-198 (FileStatusIcon), ~252-277 (BuildStatusIndicator)
- **Changes**: 
  - File status: Removed, Renamed, Copied
  - Build status: Failure, Error

---

## Testing Checklist

After implementation:
- [ ] Icons render correctly in light theme
- [ ] Icons render correctly in dark theme
- [ ] All icons have proper content descriptions
- [ ] TalkBack reads icon meanings correctly
- [ ] Icons are same size (24dp)
- [ ] Colors match Material3 theme
- [ ] No build errors or warnings
- [ ] All unit tests pass

---

## Rollback Commands

If problems arise:

```bash
# Revert code changes
git checkout HEAD -- app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/PRActionToolbar.kt
git checkout HEAD -- app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/FileListView.kt

# Remove dependency (if added)
# Edit app/build.gradle.kts and remove:
# implementation("androidx.compose.material:material-icons-extended:X.X.X")

# Sync Gradle
./gradlew --refresh-dependencies
```

---

## Quick Decision Guide

**Choose Option A if**:
- Minimal dependencies are highest priority
- Willing to accept some UX confusion
- APK size is extremely constrained

**Choose Option B if**: ⭐ RECOMMENDED
- User experience is important
- 4MB dependency is acceptable
- Want industry-standard solution
- Need future icon flexibility

**Choose Option C if**:
- Have design resources available
- Want complete control over icons
- Willing to maintain custom assets
- Need absolute minimal size

---

For detailed information, see:
- `ICON_IMPROVEMENT_PROPOSAL.md` - Full analysis
- `ICON_COMPARISON_VISUAL.md` - Visual comparison
- `ICON_IMPLEMENTATION_GUIDE.md` - Implementation steps
- `ICON_IMPROVEMENT_SUMMARY.md` - Executive summary
