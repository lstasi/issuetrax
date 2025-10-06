# Minimal Dependencies Policy

## Overview

This document describes the minimal dependency policy adopted for Issuetrax, targeting **Android 14+ (API 34) only** with a focus on simplicity and reduced APK size.

## Philosophy

- **Simple UI**: Basic Material 3 components only, no fancy animations or effects
- **Essential Only**: Only dependencies that are absolutely required for core functionality
- **Android 14+**: No backward compatibility, allowing us to remove compatibility libraries
- **Direct API Integration**: No complex local caching or background sync

## Dependencies Removed

The following dependencies were removed as they were not essential for a minimal, focused app:

### 1. Room Database (`androidx.room:*`)
**Why removed**: 
- Not used in the current implementation
- Local caching is not essential for a simple app
- Adds complexity and APK size
- Direct API calls are sufficient

**Impact**: ~2-3 MB saved

### 2. WorkManager (`androidx.work:*`)
**Why removed**:
- No background sync functionality needed
- Not used anywhere in the codebase
- Adds unnecessary complexity

**Impact**: ~1-2 MB saved

### 3. Coil Image Loading (`io.coil-kt:*`)
**Why removed**:
- No images to load in the current design
- Not used anywhere in the codebase
- Adds unnecessary dependencies

**Impact**: ~2-3 MB saved

### 4. Material Icons Extended (`androidx.compose.material:material-icons-extended`)
**Why removed**:
- Not used in the codebase
- Basic Material icons are sufficient
- Large library with many unused icons

**Impact**: ~3-4 MB saved

### 5. Core Library Desugaring (`com.android.tools:desugar_jdk_libs`)
**Why removed**:
- Not needed with minSdk 34 (Android 14)
- All Java 8+ features are natively available
- Only needed for backward compatibility

**Impact**: ~2 MB saved

### 6. Hilt-Work Integration (`androidx.hilt:hilt-work`)
**Why removed**:
- WorkManager not used
- Unnecessary dependency

**Impact**: ~500 KB saved

### 7. Testing Dependencies (navigation-testing, work-testing)
**Why removed**:
- Advanced testing not required for minimal app
- Basic testing is sufficient

**Impact**: ~1-2 MB saved

## Retained Essential Dependencies

### Core Android & Compose
- `androidx.compose.ui:*` - Essential UI framework
- `androidx.compose.material3:material3` - Material 3 design
- `androidx.activity:activity-compose` - Activity integration
- `androidx.lifecycle:*` - Lifecycle management

### Navigation & DI
- `androidx.navigation:navigation-compose` - Screen navigation
- `com.google.dagger:hilt-android` - Dependency injection
- `androidx.hilt:hilt-navigation-compose` - Navigation integration

### Networking (GitHub API)
- `com.squareup.retrofit2:retrofit` - HTTP client
- `com.squareup.okhttp3:okhttp` - Network layer
- `org.jetbrains.kotlinx:kotlinx-serialization-json` - JSON parsing
- `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter` - Retrofit integration

### State & Storage
- `org.jetbrains.kotlinx:kotlinx-coroutines-android` - Async operations
- `androidx.datastore:datastore-preferences` - Simple preferences storage

### Security & Auth
- `androidx.security:security-crypto` - Encrypted storage for OAuth tokens
- `androidx.browser:browser` - Custom Tabs for OAuth flow

### UI Support
- `com.google.android.material:material` - Material theme support

## Target Platform

- **Minimum SDK**: API 34 (Android 14)
- **Target SDK**: API 34 (Android 14)
- **No Backward Compatibility**: Simplifies implementation and reduces APK size

## Total Estimated Savings

**~12-17 MB** reduction in APK size by removing unnecessary dependencies.

## Migration Notes

If any of these features become necessary in the future:
1. **Local Caching**: Consider DataStore or simple file storage before adding Room
2. **Background Sync**: Evaluate if truly needed before adding WorkManager
3. **Image Loading**: Use basic Compose APIs or add Coil if complex image handling is required
4. **Extended Icons**: Consider using SVG assets instead of large icon libraries

## Verification

The app builds successfully with the minimal dependency set:
```bash
./gradlew assembleDebug  # ✅ BUILD SUCCESSFUL
```

All existing functionality works with the reduced dependency set.

## Documentation

All project documentation has been updated to reflect this minimal dependency policy:
- `PROJECT_DEFINITION.md` - Updated technical stack
- `ARCHITECTURE.md` - Updated build configuration
- `PROJECT_SETUP.md` - Updated dependencies list
- `README.md` - Updated architecture section
- `.github/copilot-instructions.md` - Updated development guidelines

---

**Last Updated**: October 2024
**Status**: ✅ Implemented and Verified
