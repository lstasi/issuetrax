# Testing Release Builds in Emulator

## To reproduce the error in the emulator:

### Method 1: Build and Install Release APK

```bash
# Build the release APK
./gradlew assembleRelease

# Install it on the emulator/device
adb install app/build/outputs/apk/release/app-release.apk
```

### Method 2: Run Release Build Variant

```bash
# Run the release variant directly
./gradlew installRelease

# Or via Android Studio:
# 1. Open Build Variants panel (View > Tool Windows > Build Variants)
# 2. Change "Active Build Variant" from "debug" to "release"
# 3. Run the app normally
```

### Method 3: Add a Debug Build Type with Minification

Add this to your `build.gradle.kts` buildTypes block:

```kotlin
buildTypes {
    debug {
        isMinifyEnabled = true
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
    release {
        // ... existing config
    }
}
```

This will enable ProGuard/R8 even in debug builds, allowing you to test obfuscation issues in the emulator.

## Verifying the Fix

After applying the ProGuard rule fixes:

1. Clean and rebuild: `./gradlew clean assembleRelease`
2. Install on device: `adb install app/build/outputs/apk/release/app-release.apk`
3. Test the repository screen
4. The error should no longer appear

