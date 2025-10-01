# Copilot Coding Agent Setup

This directory contains setup scripts for the GitHub Copilot Coding Agent environment.

## copilot-setup.sh

This script runs **before the firewall is enabled** in the Copilot environment, allowing it to access external repositories like `dl.google.com` to download necessary dependencies.

### What it does:

1. Downloads the Gradle wrapper
2. Pre-downloads Android Gradle Plugin from Google's Maven repository
3. Caches all project dependencies
4. Sets up the build environment

### Usage:

The script is automatically executed by the Copilot Coding Agent when configured in the repository settings:

1. Go to Repository Settings → Copilot → Coding Agent
2. Under "Actions setup steps", add this script to run before the firewall is enabled

Alternatively, run manually:
```bash
./.github/copilot-setup.sh
```

### Why this is needed:

The Copilot Coding Agent environment has a firewall that blocks access to `dl.google.com` and other external repositories after initialization. This script ensures all required dependencies are downloaded and cached before the firewall is enabled.

### Build Configuration:

The project uses a `buildscript` block in `build.gradle.kts` to properly resolve the Android Gradle Plugin from Google's Maven repository:

```kotlin
buildscript {
    repositories {
        google()  // Resolves to dl.google.com
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        // ...
    }
}
```

This configuration works with the `pluginManagement` block in `settings.gradle.kts` to ensure proper dependency resolution.
