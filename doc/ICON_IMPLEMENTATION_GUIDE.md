# Icon Improvement Implementation Guide

This guide provides step-by-step instructions for implementing the icon improvements in the PR review interface.

## Prerequisites

Read the following documents first:
- `doc/ICON_IMPROVEMENT_PROPOSAL.md` - Full analysis and rationale
- `doc/ICON_COMPARISON_VISUAL.md` - Visual before/after comparison

## Implementation Approach

Based on the analysis, we recommend **Approach B: Add Extended Icons** for significant UX improvement.

---

## Step 1: Add Material Icons Extended Dependency

### 1.1 Update build.gradle.kts

Add the dependency to `app/build.gradle.kts`:

```kotlin
dependencies {
    // ... existing dependencies ...
    
    // Material Icons Extended (for better PR review icons)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    
    // ... rest of dependencies ...
}
```

Note: Check for the latest version at https://developer.android.com/jetpack/androidx/releases/compose-material

### 1.2 Sync Gradle

Run:
```bash
./gradlew --refresh-dependencies
```

---

## Step 2: Update PRActionToolbar.kt

### 2.1 Add New Imports

```kotlin
// Add these imports at the top of PRActionToolbar.kt
import androidx.compose.material.icons.filled.CallMerge
import androidx.compose.material.icons.filled.Comment
```

### 2.2 Change Merge Button Icon

Find line ~84 and change:
```kotlin
// BEFORE:
imageVector = Icons.Default.Add,

// AFTER:
imageVector = Icons.Filled.CallMerge,
```

Update content description:
```kotlin
contentDescription = "Merge PR",
```

### 2.3 Change Comment Button Icon

Find line ~116 and change:
```kotlin
// BEFORE:
imageVector = Icons.Default.Edit,

// AFTER:
imageVector = Icons.Filled.Comment,
```

### 2.4 Update Dialog Icons

Find line ~354 (Merge dialog icon) and change:
```kotlin
// BEFORE:
imageVector = Icons.Default.Add,

// AFTER:
imageVector = Icons.Filled.CallMerge,
```

Find line ~411 (Comment dialog icon) and change:
```kotlin
// BEFORE:
imageVector = Icons.Default.Edit,

// AFTER:
imageVector = Icons.Filled.Comment,
```

---

## Step 3: Update FileListView.kt

### 3.1 Add New Imports

```kotlin
// Add these imports at the top of FileListView.kt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DriveFileRenameOutline
```

### 3.2 Update FileStatusIcon Function

Find the `FileStatusIcon` composable (around line 168) and update:

```kotlin
@Composable
private fun FileStatusIcon(
    status: FileStatus,
    modifier: Modifier = Modifier
) {
    val (icon, backgroundColor, iconColor) = when (status) {
        FileStatus.ADDED -> Triple(
            Icons.Default.Add,  // Keep as-is
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        FileStatus.MODIFIED, FileStatus.CHANGED -> Triple(
            Icons.Default.Edit,  // Keep as-is
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary
        )
        FileStatus.REMOVED -> Triple(
            Icons.Filled.Delete,  // CHANGED: Close → Delete
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.onError
        )
        FileStatus.RENAMED -> Triple(
            Icons.Filled.DriveFileRenameOutline,  // CHANGED: Edit → DriveFileRenameOutline
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )
        FileStatus.COPIED -> Triple(
            Icons.Filled.ContentCopy,  // CHANGED: Edit → ContentCopy
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )
        FileStatus.UNCHANGED -> Triple(
            Icons.Default.Edit,  // Keep as-is
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Icon(
        imageVector = icon,
        contentDescription = status.name,
        modifier = modifier
            .size(32.dp)
            .background(backgroundColor, CircleShape)
            .padding(6.dp),
        tint = iconColor
    )
}
```

### 3.3 Update BuildStatusIndicator Function

Find the `BuildStatusIndicator` composable (around line 242) and update:

```kotlin
@Composable
private fun BuildStatusIndicator(
    status: com.issuetrax.app.domain.entity.CommitStatus,
    modifier: Modifier = Modifier
) {
    data class StatusDisplay(
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val backgroundColor: androidx.compose.ui.graphics.Color,
        val iconColor: androidx.compose.ui.graphics.Color,
        val text: String
    )
    
    val display = when (status.state) {
        com.issuetrax.app.domain.entity.CommitState.SUCCESS -> StatusDisplay(
            icon = Icons.Default.CheckCircle,  // Keep as-is
            backgroundColor = MaterialTheme.colorScheme.primary,
            iconColor = MaterialTheme.colorScheme.onPrimary,
            text = "Checks passed"
        )
        com.issuetrax.app.domain.entity.CommitState.FAILURE -> StatusDisplay(
            icon = Icons.Filled.Error,  // CHANGED: Close → Error
            backgroundColor = MaterialTheme.colorScheme.error,
            iconColor = MaterialTheme.colorScheme.onError,
            text = "Checks failed"
        )
        com.issuetrax.app.domain.entity.CommitState.PENDING -> StatusDisplay(
            icon = Icons.Default.Refresh,  // Keep as-is
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            iconColor = MaterialTheme.colorScheme.onTertiary,
            text = "Checks pending"
        )
        com.issuetrax.app.domain.entity.CommitState.ERROR -> StatusDisplay(
            icon = Icons.Filled.Error,  // CHANGED: Close → Error
            backgroundColor = MaterialTheme.colorScheme.error,
            iconColor = MaterialTheme.colorScheme.onError,
            text = "Checks error"
        )
    }
    
    // ... rest of function ...
}
```

---

## Step 4: Test the Changes

### 4.1 Build the Project

```bash
./gradlew assembleDebug
```

### 4.2 Run Unit Tests

```bash
./gradlew testDebugUnitTest
```

### 4.3 Run Instrumentation Tests (if applicable)

```bash
./gradlew connectedDebugAndroidTest
```

### 4.4 Manual Visual Testing

1. Run the app on an emulator or device
2. Navigate to a PR review screen
3. Verify all icons render correctly:
   - PRActionToolbar icons
   - File status icons in file list
   - Build status indicators
4. Test both light and dark themes
5. Verify accessibility with TalkBack

---

## Step 5: Update Tests (if needed)

### 5.1 Check PRActionToolbarTest.kt

If any tests verify icon types, update them to expect new icons:

```kotlin
// Example: If tests check icon types
@Test
fun `merge button should use CallMerge icon`() {
    // Test implementation
}
```

### 5.2 Check FileListViewTest.kt

Update any tests that verify file status icons.

---

## Step 6: Document the Changes

### 6.1 Update Code Comments

Add comments explaining icon choices:

```kotlin
// Using CallMerge icon instead of Add to clearly indicate branch merging
Icon(imageVector = Icons.Filled.CallMerge, ...)
```

### 6.2 Store Memory Fact

Document the icon choices for future reference:

```
Fact: PR review interface uses Material Icons Extended for semantically meaningful icons
Rationale: CallMerge for merge actions, Comment for commenting, Delete for removal
Source: doc/ICON_IMPROVEMENT_PROPOSAL.md
```

---

## Step 7: Verify APK Size Impact

### 7.1 Build Release APK

```bash
./gradlew assembleRelease
```

### 7.2 Check APK Size

```bash
ls -lh app/build/outputs/apk/release/
```

### 7.3 Verify ProGuard Optimization

Check that ProGuard removes unused icons:

```bash
./gradlew assembleRelease --info | grep "material-icons"
```

---

## Rollback Plan

If issues arise, revert changes:

### Remove Dependency

In `app/build.gradle.kts`, remove:
```kotlin
implementation("androidx.compose.material:material-icons-extended:1.5.4")
```

### Revert Icon Changes

Use git to revert the icon changes in:
- `PRActionToolbar.kt`
- `FileListView.kt`

```bash
git checkout HEAD -- app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/PRActionToolbar.kt
git checkout HEAD -- app/src/main/java/com/issuetrax/app/presentation/ui/pr_review/components/FileListView.kt
```

---

## Alternative: Core-Only Implementation

If extended icons are not approved, implement only the core icon changes:

### Changes Using Core Icons Only

1. **File Removed**: Close → Delete
2. **Build Failure**: Close → Error

Update only these in `FileListView.kt`:

```kotlin
// Add imports
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error

// Update FileStatus.REMOVED
FileStatus.REMOVED -> Triple(
    Icons.Filled.Delete,  // CHANGED
    MaterialTheme.colorScheme.error,
    MaterialTheme.colorScheme.onError
)

// Update CommitState.FAILURE
com.issuetrax.app.domain.entity.CommitState.FAILURE -> StatusDisplay(
    icon = Icons.Filled.Error,  // CHANGED
    backgroundColor = MaterialTheme.colorScheme.error,
    iconColor = MaterialTheme.colorScheme.onError,
    text = "Checks failed"
)
```

**No dependency change required.**

---

## Success Criteria

✅ All icons render correctly  
✅ No build errors or warnings  
✅ All tests pass  
✅ Icons are semantically meaningful  
✅ Accessibility content descriptions are clear  
✅ Works in both light and dark themes  
✅ APK size increase is acceptable (<5MB)  
✅ Icons follow Material Design guidelines  

---

## Questions or Issues?

Refer to:
- `doc/ICON_IMPROVEMENT_PROPOSAL.md` for detailed analysis
- `doc/ICON_COMPARISON_VISUAL.md` for visual comparison
- [Material Design Icons](https://fonts.google.com/icons) for icon reference
- [Compose Material Icons](https://developer.android.com/reference/kotlin/androidx/compose/material/icons/package-summary) for API docs
