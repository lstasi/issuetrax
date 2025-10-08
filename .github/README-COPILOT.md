# Copilot Coding Agent Setup

This directory contains configuration and setup scripts for the GitHub Copilot Coding Agent environment.

## copilot.yml

Configuration file for GitHub Copilot Coding Agent that defines:
- Setup steps to run before the firewall is enabled
- Custom instructions file location
- Build environment configuration

See [Best practices for Copilot coding agent](https://gh.io/copilot-coding-agent-tips) for more information.

## copilot-setup.sh

This script runs **before the firewall is enabled** in the Copilot environment, allowing it to access external repositories like `dl.google.com` to download necessary dependencies.

### What it does:

1. Downloads the Gradle wrapper
2. Pre-downloads Android Gradle Plugin from Google's Maven repository
3. Caches all project dependencies
4. Sets up the build environment

### Usage:

The script is automatically executed by the Copilot Coding Agent based on the `copilot.yml` configuration file.

To run manually for testing:
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
